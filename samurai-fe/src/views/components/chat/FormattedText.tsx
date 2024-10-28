import { TypographyStylesProvider } from "@mantine/core";
import DOMPurify from "dompurify";
import { marked, MarkedOptions } from "marked";

const defaultMarkedConfig = { breaks: true } as MarkedOptions & {
  async: false;
};

export interface MarkdownFormatterProps {
  markdown?: string;
  options?: MarkedOptions;
}

export default function FormattedText(props: MarkdownFormatterProps) {
  return (
    <TypographyStylesProvider>
      <div
        dangerouslySetInnerHTML={{
          __html: DOMPurify.sanitize(
            marked.parse(
              props.markdown ?? "",
              props.options ?? defaultMarkedConfig,
            ) as string,
          ),
        }}
      />
    </TypographyStylesProvider>
  );
}
