import { Mark, Text } from "@mantine/core";

interface HighlightedTextProps {
  text?: string;
  phrase?: string;
}

export default function HighlightedText(props: HighlightedTextProps) {
  if (props.text && props.phrase) {
    const index = props.text.toLowerCase().indexOf(props.phrase.toLowerCase());
    if (index !== -1) {
      return (
        <Text fz={"inherit"} span>
          {props.text.substring(0, index)}
          <Mark>
            {props.text.substring(index, index + props.phrase.length)}
          </Mark>
          {props.text.substring(index + props.phrase.length)}
        </Text>
      );
    }
  }
  return (
    <Text fz={"inherit"} span>
      {props.text}
    </Text>
  );
}
