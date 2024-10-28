import { Flex, TextInput } from "@mantine/core";
import { IconCheck, IconEdit, IconTrash, IconX } from "@tabler/icons-react";
import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";

import { useAppDispatch } from "../../../hooks/useAppDispatch.ts";
import { JsonPatch } from "../../../model/api/patch/JsonPatch.ts";
import {
  deleteConversation,
  patchConversation,
  validateSummary,
} from "../../../model/service/conversationService.ts";
import {
  editConversationSummary,
  removeConversation,
} from "../../../reducers/conversationsSlice.ts";
import { showErrorMessage } from "../../../utils.ts";
import classes from "./Summary.module.css";
import { JsonPatchNodeImpl } from "../../../model/api/patch/JsonPatchNodeImpl.ts";

interface SummaryProps {
  id: string;
  summary: string;
  active: boolean;
}

export default function Summary(props: SummaryProps) {
  const navigate = useNavigate();
  const inputRef = useRef<HTMLInputElement>(null);
  const dispatch = useAppDispatch();
  const [editMode, setEditMode] = useState(false);
  const [deleteMode, setDeleteMode] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    inputRef.current!.value = props.summary;
    setEditMode(false);
    setDeleteMode(false);
    setError("");
  }, [props.summary]);

  useEffect(() => {}, [editMode, deleteMode]);

  const submitChange = (e: any) => {
    setError("");
    e.preventDefault();
    if (editMode) {
      const error = validateSummary(inputRef.current!.value);
      if (error) {
        setError(error);
        return;
      }

      const patchNode = JsonPatchNodeImpl.replace(
        "/summary",
        inputRef.current!.value,
      );

      patchConversation(props.id, JsonPatch.of(patchNode))
        .then(() =>
          dispatch(
            editConversationSummary({
              id: props.id,
              summary: inputRef.current!.value,
            }),
          ),
        )
        .catch(() => showErrorMessage("Failed to change conversation summary"))
        .finally(() => setEditMode(false));
    }
    if (deleteMode) {
      deleteConversation(props.id)
        .then(() => {
          dispatch(removeConversation(props.id));
          if (props.active) navigate("/conversations/new");
        })
        .catch(() => showErrorMessage("Failed to delete conversation"))
        .finally(() => setDeleteMode(false));
    }
  };

  const cancelChange = (e: React.MouseEvent<SVGSVGElement, MouseEvent>) => {
    e.preventDefault();
    if (editMode) {
      inputRef.current!.value = props.summary;
      setEditMode(false);
      setError("");
    }
    if (deleteMode) {
      setDeleteMode(false);
    }
  };

  return (
    <form onSubmit={(e) => submitChange(e)}>
      <TextInput
        className={classes.summaryInput}
        ref={inputRef}
        type="text"
        size="sm"
        bd={
          props.active && !editMode && !error
            ? "1px solid var(--mantine-primary-color-filled)"
            : "none"
        }
        defaultValue={props.summary}
        readOnly={!(props.active && editMode)}
        rightSectionWidth={50}
        rightSection={
          props.active &&
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
                color="green"
              />
              <IconX
                size={20}
                cursor="pointer"
                onClick={(e) => cancelChange(e)}
                color="red"
              />
            </Flex>
          ))
        }
        onClick={(e) => {
          e.preventDefault();
          navigate(`/conversations/${props.id}`);
        }}
        error={error}
      />
    </form>
  );
}
