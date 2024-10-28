import { Message } from "../../../../model/api/message/Message.ts";
import { MessageStatus } from "../../../../model/api/message/MessageStatus.ts";
import { MessageType } from "../../../../model/api/message/MessageType.ts";
import AssistantMessage from "./AssistantMessage.tsx";
import UserMessage from "./UserMessage.tsx";

export default function ChatMessage(props: Message) {
  switch (props.type) {
    case MessageType.USER:
      return <UserMessage {...props} />;
    case MessageType.ASSISTANT:
      return <AssistantMessage {...props} />;
    default:
      return (
        <AssistantMessage
          id=""
          type={MessageType.ASSISTANT}
          status={MessageStatus.ERROR}
          content="Something went wrong, try again later"
        />
      );
  }
}
