import { Center, Loader, Select } from "@mantine/core";
import { useEffect } from "react";
import { useSelector } from "react-redux";
import { useAppDispatch } from "../../../hooks.ts";
import {
  fetchAvailableAssistants,
  setActiveAssistant,
} from "../../../reducers/assistantSlice.ts";
import { setActiveConversation } from "../../../reducers/conversationsSlice.ts";
import { RootState } from "../../../store.ts";

export default function SelectAssistant() {
  const dispatch = useAppDispatch();
  const assistant = useSelector((state: RootState) => state.assistant);

  useEffect(() => {
    dispatch(fetchAvailableAssistants());
  }, []);

  useEffect(() => {}, [assistant]);

  return (
    <>
      {assistant.loading && !assistant.currentId ? (
        <Center>
          <Loader />
        </Center>
      ) : (
        <Select
          checkIconPosition="right"
          placeholder="Select assistant"
          data={assistant.available.map((assistant) => ({
            value: assistant.id,
            label: assistant.name,
          }))}
          defaultValue={assistant.currentId}
          allowDeselect={false}
          onChange={(value) => {
            dispatch(setActiveAssistant(value));
            dispatch(setActiveConversation(undefined));
          }}
        />
      )}
    </>
  );
}
