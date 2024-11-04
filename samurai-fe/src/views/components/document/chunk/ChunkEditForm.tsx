import { Box, Button, NumberInput, Stack, Text, Textarea } from "@mantine/core";
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
import { modals } from "@mantine/modals";

interface ChunkEditFormProps {
  current?: Chunk;
  onSubmitPatch?: (id: string, patch: JsonPatch) => void;
  onSubmitAdd?: (chunk: UploadChunk) => void;
  defaultPosition?: number;
  maxPosition: number;
}

export function showChunkEditForm(props: ChunkEditFormProps) {
  modals.open({
    title: (
      <Text fz="h3" fw="bold" span>
        {props.onSubmitAdd ? "Add" : "Edit"} chunk
      </Text>
    ),
    children: <ChunkEditForm {...props} />,
    size: "xl",
  });
}

export default function ChunkEditForm(props: ChunkEditFormProps) {
  const form = useForm({
    initialValues: {
      position:
        props.defaultPosition !== undefined
          ? props.defaultPosition + 1
          : props.current !== undefined
            ? props.current.position + 1
            : props.maxPosition,
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
      jsonPatch.add(
        JsonPatchNodeImpl.replace("/position", values.position - 1),
      );
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
      position: values.position - 1,
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
            min={1}
            max={props.maxPosition}
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
