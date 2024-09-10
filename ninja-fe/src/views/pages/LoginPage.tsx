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
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { useAppDispatch } from "../../hooks.ts";
import { Login } from "../../model/api/Login.ts";
import { authenticate } from "../../reducers/authSlice.ts";
import { resetConversations } from "../../reducers/conversationsSlice.ts";
import { RootState } from "../../store.ts";

export default function LoginPage() {
  useDocumentTitle("Sign in | DocsNinja");
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const auth = useSelector((state: RootState) => state.auth);

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
    dispatch(resetConversations());
    dispatch(authenticate(login));
  };

  return (
    <Container size={420} my={40}>
      <Title ta="center">Welcome back!</Title>
      <Text c="dimmed" size="sm" ta="center" mt={5}>
        Do not have an account yet?{" "}
        <Anchor size="sm" component="button">
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
            <Anchor component="button" size="sm"></Anchor>
            Forgot password?
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
