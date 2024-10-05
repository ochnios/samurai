import { notifications } from "@mantine/notifications";

export function showNotImplementedMessage() {
  notifications.show({
    color: "violet",
    title: "Info",
    message: "This feature not implemented yet",
  });
}

export function showErrorMessage(message: string) {
  notifications.show({
    color: "red",
    title: "Error",
    message: message,
  });
}
