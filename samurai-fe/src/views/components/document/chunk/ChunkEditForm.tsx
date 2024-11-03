import { Box, Button, NumberInput, Stack, Textarea } from "@mantine/core";
import { useForm } from "@mantine/form";
import {
  validateContent,
  validatePosition,
} from "../../../../model/service/chunkService.ts";
import { UploadChunk } from "../../../../model/api/document/chunk/UploadChunk.ts";
import { showErrorMessage } from "../../../../utils.ts";
import { Chunk } from "../../../../model/api/document/chunk/Chunk.ts";
import { JsonPatch } from "../../../../model/api/patch/JsonPatch.ts";
import { JsonPatchNodeImpl } from "../../../../model/api/patch/JsonPatchNodeImpl.ts";

interface ChunkEditFormProps {
  current?: Chunk;
  onSubmitPatch?: (id: string, patch: JsonPatch) => void;
  onSubmitAdd?: (chunk: UploadChunk) => void;
}

export default function ChunkEditForm(props: ChunkEditFormProps) {
  const form = useForm({
    initialValues: {
      position: props.current?.position,
      content: props.current?.content,
    },
    validate: {
      position: validatePosition,
      content: validateContent,
    },
  });

  const handleSubmit = (values: typeof form.values) => {
    if (props.onSubmitPatch && props.current) {
      handleSubmitPatch(values);
    } else if (props.onSubmitAdd) {
      handleSubmitAdd(values);
    } else {
      showErrorMessage("Something went wrong");
    }
  };

  const handleSubmitPatch = (values: typeof form.values) => {
    const jsonPatch = JsonPatch.empty();
    if (values.position != props.current?.position) {
      jsonPatch.add(JsonPatchNodeImpl.replace("/position", values.position));
    }
    if (values.content !== props.current?.content) {
      if (values.content === "") {
        jsonPatch.add(JsonPatchNodeImpl.remove("/content"));
      } else {
        jsonPatch.add(JsonPatchNodeImpl.replace("/content", values.content));
      }
    }
    props.onSubmitPatch!(props.current!.id, jsonPatch);
  };

  const handleSubmitAdd = (values: typeof form.values) => {
    const chunk = {
      content: values.content,
      position: values.position,
    } as UploadChunk;
    props.onSubmitAdd!(chunk);
  };

  return (
    <Box>
      <form onSubmit={form.onSubmit(handleSubmit)}>
        <Stack gap="md">
          <Textarea
            label="Chunk content"
            placeholder="Enter chunk content"
            value={form.values.content}
            onChange={(event) =>
              form.setFieldValue("content", event.currentTarget.value)
            }
            resize="vertical"
            autosize
            minRows={3}
            error={form.errors.content}
          />
          <NumberInput
            label="Chunk position in document"
            placeholder="Enter chunk position"
            min={0}
            max={20} // TODO number of chunks
            value={form.values.position}
            onChange={(value) =>
              form.setFieldValue("position", value as number)
            }
            error={form.errors.position}
          />

          <Button type="submit">Save</Button>
        </Stack>
      </form>
    </Box>
  );
}
