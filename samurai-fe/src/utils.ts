import { notifications } from "@mantine/notifications";

export function showInfoMessage(message: string) {
  notifications.show({
    color: "violet",
    title: "Info",
    message: message,
  });
}

export function showErrorMessage(message: string) {
  notifications.show({
    color: "red",
    title: "Error",
    message: message,
  });
}

export function showNotImplementedMessage() {
  showInfoMessage("This feature not implemented yet");
}

export function normalizePostfix(...params: string[]): string {
  const notEmptyParams = params.filter((s) => s != "");
  if (!notEmptyParams.length) {
    return "";
  }
  const merged = notEmptyParams.join("&");
  return `?${merged}`;
}

export const defaultMantineTableContainerProps = {
  mah: {
    base: "calc(100vh - 280px)",
    xs: "calc(100vh - 230px)",
    md: "calc(100vh - 210px)",
  },
};

export function highlightMarkdown(text: string, phrase: string): string {
  if (text && phrase) {
    const index = text.toLowerCase().indexOf(phrase.toLowerCase());
    if (index !== -1) {
      return (
        text.substring(0, index) +
        "<mark>" +
        text.substring(index, index + phrase.length) +
        "</mark>" +
        text.substring(index + phrase.length)
      );
    }
  }
  return text;
}
