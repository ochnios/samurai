import { Flex, TextInput } from "@mantine/core";
import { IconCheck, IconEdit, IconTrash, IconX } from "@tabler/icons-react";
import { useEffect, useRef, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { ConversationSummary } from "../../../model/api/ConversationSummary.ts";
import { renameConversation } from "../../../reducers/conversationsSlice.ts";
import { RootState } from "../../../store.ts";
import classes from "./Conversations.module.css";

interface SummaryProps {
  summary: ConversationSummary;
}

export default function Summary(props: SummaryProps) {
  const navigate = useNavigate();
  const inputRef = useRef<HTMLInputElement>(null);
  const dispatch = useDispatch();
  const conversations = useSelector((state: RootState) => state.conversations);
  const [editMode, setEditMode] = useState(false);
  const [deleteMode, setDeleteMode] = useState(false);

  useEffect(() => {
    inputRef.current!.value = props.summary.summary;
    setEditMode(false);
    setDeleteMode(false);
  }, [conversations.currentId]);

  useEffect(() => {}, [editMode, deleteMode]);

  const isActive = (conversationId: string) => {
    return conversations.currentId === conversationId;
  };

  const submitChange = (e: React.MouseEvent<SVGSVGElement, MouseEvent>) => {
    e.preventDefault();
    if (editMode) {
      // TODO submit edit summary operation
      dispatch(
        renameConversation({
          id: props.summary.id,
          newSummary: inputRef.current!.value,
        }),
      );
      setEditMode(false);
    }
    if (deleteMode) {
      // TODO submit delete conversation operation
      setDeleteMode(false);
    }
  };

  const cancelChange = (e: React.MouseEvent<SVGSVGElement, MouseEvent>) => {
    e.preventDefault();
    if (editMode) {
      inputRef.current!.value = props.summary.summary;
      setEditMode(false);
    }
    if (deleteMode) {
      setDeleteMode(false);
    }
  };

  return (
    <TextInput
      ref={inputRef}
      type="text"
      size="sm"
      defaultValue={props.summary.summary}
      readOnly={!(isActive(props.summary.id) && editMode)}
      rightSectionWidth={50}
      rightSection={
        isActive(props.summary.id) &&
        (!editMode && !deleteMode ? (
          <Flex>
            <IconEdit
              size={20}
              cursor="pointer"
              onClick={(e) => {
                e.preventDefault();
                setEditMode(true);
              }}
            />
            <IconTrash
              size={20}
              cursor="pointer"
              onClick={(e) => {
                e.preventDefault();
                setDeleteMode(true);
              }}
            />
          </Flex>
        ) : (
          <Flex>
            <IconCheck
              size={20}
              cursor="pointer"
              onClick={(e) => submitChange(e)}
            />
            <IconX
              size={20}
              cursor="pointer"
              onClick={(e) => cancelChange(e)}
            />
          </Flex>
        ))
      }
      className={classes.conversationInput}
      onClick={(e) => {
        e.preventDefault();
        navigate(`/conversations/${props.summary.id}`);
      }}
    />
  );
}
