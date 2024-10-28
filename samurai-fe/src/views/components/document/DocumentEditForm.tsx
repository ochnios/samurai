import { Box, Button, Stack, Textarea, TextInput } from "@mantine/core";
import { useForm } from "@mantine/form";
import {
  validateDescription,
  validateTitle,
} from "../../../model/service/documentService.ts";
import { Document } from "../../../model/api/document/Document.ts";
import { JsonPatch } from "../../../model/api/patch/JsonPatch.ts";
import { JsonPatchNodeImpl } from "../../../model/api/patch/JsonPatchNodeImpl.ts";

interface DocumentEditFormProps {
  current: Document;
  onSubmit: (id: string, patch: JsonPatch) => void;
}

export default function DocumentEditForm(props: DocumentEditFormProps) {
  const form = useForm({
    initialValues: {
      title: props.current.title,
      description: props.current.description,
    },
    validate: {
      title: validateTitle,
      description: validateDescription,
    },
  });

  const handleSubmit = (values: typeof form.values) => {
    const jsonPatch = JsonPatch.empty();
    if (values.title != props.current.title) {
      jsonPatch.add(JsonPatchNodeImpl.replace("/title", values.title));
    }
    if (values.description !== props.current.description) {
      if (values.description === "") {
        jsonPatch.add(JsonPatchNodeImpl.remove("/description"));
      } else {
        jsonPatch.add(
          JsonPatchNodeImpl.replace("/description", values.description),
        );
      }
    }
    props.onSubmit(props.current.id, jsonPatch);
  };

  return (
    <Box>
      <form onSubmit={form.onSubmit(handleSubmit)}>
        <Stack gap="md">
          <TextInput
            label="Title"
            placeholder="Document title"
            value={form.values.title}
            onChange={(event) =>
              form.setFieldValue("title", event.currentTarget.value)
            }
            error={form.errors.title}
            required
          />
          <Textarea
            label="Document description"
            placeholder="Enter document description"
            value={form.values.description}
            onChange={(event) =>
              form.setFieldValue("description", event.currentTarget.value)
            }
            resize="vertical"
            autosize
            minRows={3}
            error={form.errors.description}
          />
          <Button type="submit">Save</Button>
        </Stack>
      </form>
    </Box>
  );
}
