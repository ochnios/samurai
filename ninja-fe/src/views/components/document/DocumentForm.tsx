import {
  Box,
  Button,
  Checkbox,
  FileInput,
  Stack,
  Textarea,
  TextInput,
} from "@mantine/core";
import { useForm } from "@mantine/form";
import {
  MAX_DESCRIPTION_LENGTH,
  MAX_FILE_SIZE,
  MAX_TITLE_LENGTH,
  MIN_DESCRIPTION_LENGTH,
  MIN_TITLE_LENGTH,
} from "../../../model/service/documentService.ts";

interface DocumentUpload {
  file: File;
  autogenerateDescription?: boolean;
  title: string;
  description?: string | null;
}

interface DocumentFormProps {
  onSubmit: (document: DocumentUpload) => void;
}

export default function DocumentForm(props: DocumentFormProps) {
  const form = useForm({
    initialValues: {
      file: null as File | null,
      title: "",
      description: "",
      autogenerateDescription: false,
    },
    validate: {
      file: (value: File | null) =>
        value && value.size < MAX_FILE_SIZE
          ? null
          : "File is required and should have up to 50 MB",
      title: (value: string) => {
        const titleLength = value.trim().length;
        return titleLength >= MIN_TITLE_LENGTH && titleLength < MAX_TITLE_LENGTH
          ? null
          : `Titles should be between ${MIN_TITLE_LENGTH} and ${MAX_TITLE_LENGTH} characters`;
      },
      description: (value: string) => {
        const descriptionLength = value.trim().length;
        return descriptionLength == 0 ||
          (descriptionLength >= MIN_DESCRIPTION_LENGTH &&
            descriptionLength < MAX_DESCRIPTION_LENGTH)
          ? null
          : `Description should be between ${MIN_DESCRIPTION_LENGTH} and ${MAX_DESCRIPTION_LENGTH} characters`;
      },
    },
  });

  const handleFileChange = (file: File | null) => {
    if (file) {
      const fileTitle = file.name.replace(/\.[^/.]+$/, ""); // Remove file extension
      form.setFieldValue("file", file);
      form.setFieldValue("title", fileTitle);
    } else {
      form.setFieldValue("title", "");
    }
  };

  const handleSubmit = (values: typeof form.values) => {
    const document: DocumentUpload = {
      file: values.file as File,
      title: values.title,
      autogenerateDescription: values.autogenerateDescription,
      description: null,
    };
    props.onSubmit(document);
  };

  return (
    <Box>
      <form onSubmit={form.onSubmit(handleSubmit)}>
        <Stack gap="md">
          <FileInput
            label="Upload Document"
            description="Only PDF documents are supported, up to 50MB"
            placeholder="Choose file"
            accept="application/pdf"
            value={form.values.file}
            onChange={handleFileChange}
            error={form.errors.file}
            required
          />
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
            disabled={form.values.autogenerateDescription}
            resize="vertical"
            autosize
            minRows={3}
            error={form.errors.description}
          />
          <Checkbox
            label="Generate description automatically"
            description="If checked, description will be generated automatically basing on document content"
            checked={form.values.autogenerateDescription}
            onChange={(event) =>
              form.setFieldValue(
                "autogenerateDescription",
                event.currentTarget.checked,
              )
            }
          />
          <Button type="submit">Upload</Button>
        </Stack>
      </form>
    </Box>
  );
}
