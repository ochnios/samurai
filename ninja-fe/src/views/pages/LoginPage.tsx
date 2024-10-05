import {
  Anchor,
  Button,
  Checkbox,
  Container,
  Group,
  Paper,
  PasswordInput,
  Text,
  TextInput,
  Title,
} from "@mantine/core";
import { useDocumentTitle } from "@mantine/hooks";
import { notifications } from "@mantine/notifications";
import { useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { useAppDispatch } from "../../hooks/useAppDispatch.ts";
import { useAppSelector } from "../../hooks/useAppSelector.ts";
import { Login } from "../../model/api/Login.ts";
import { authenticate } from "../../reducers/authSlice.ts";
import { resetConversationList } from "../../reducers/conversationsSlice.ts";
import { RootState } from "../../store.ts";
import { showNotImplementedMessage } from "../../utils.ts";

export default function LoginPage() {
  useDocumentTitle("Sign in | DocsNinja");
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const auth = useAppSelector((state: RootState) => state.auth);

  const usernameRef = useRef<HTMLInputElement>(null);
  const passwordRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (auth.loading) return;
    else if (auth.authenticated) navigate("/");
    else if (auth.errors) {
      notifications.show({
        color: "red",
        title: "Error",
        message: "Wrong username or password",
      });
    }
  }, [auth]);

  const handleSubmit = (e: any) => {
    e.preventDefault();
    const login: Login = {
      username: usernameRef.current!.value,
      password: passwordRef.current!.value,
    };
    dispatch(resetConversationList());
    dispatch(authenticate(login));
  };

  return (
    <Container size={420} my={40}>
      <Title ta="center">Welcome back!</Title>
      <Text c="dimmed" size="sm" ta="center" mt={5}>
        Do not have an account yet?{" "}
        <Anchor size="sm" onClick={showNotImplementedMessage}>
          Create account
        </Anchor>
      </Text>
      <Paper withBorder shadow="md" p={30} mt={30} radius="md">
        <form onSubmit={handleSubmit}>
          <TextInput
            ref={usernameRef}
            label="Username"
            placeholder="Your username"
            required
          />
          <PasswordInput
            ref={passwordRef}
            label="Password"
            placeholder="Your password"
            required
            mt="md"
          />
          <Group justify="space-between" mt="lg">
            <Anchor size="sm" onClick={showNotImplementedMessage}>
              Forgot password?
            </Anchor>
            <Checkbox label="Remember me" />
          </Group>
          <Button fullWidth mt="xl" type="submit">
            Sign in
          </Button>
        </form>
      </Paper>
    </Container>
  );
}
