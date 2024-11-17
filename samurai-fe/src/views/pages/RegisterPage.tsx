import {
  Anchor,
  Button,
  Container,
  Paper,
  PasswordInput,
  Text,
  TextInput,
  Title,
} from "@mantine/core";
import { useDocumentTitle } from "@mantine/hooks";
import { useNavigate } from "react-router-dom";
import { useForm } from "@mantine/form";
import {
  registerCall,
  validateConfirmPassword,
  validateEmail,
  validateName,
  validatePassword,
  validateUsername,
} from "../../model/service/authService";
import { Register } from "../../model/api/auth/Register";
import { showErrorMessage, showInfoMessage } from "../../utils";

export default function RegisterPage() {
  useDocumentTitle("Create account | SamurAI");
  const navigate = useNavigate();

  const form = useForm({
    initialValues: {
      username: "",
      firstname: "",
      lastname: "",
      email: "",
      password: "",
      confirmPassword: "",
    },
    validate: {
      username: validateUsername,
      firstname: (value) => validateName(value, "Firstname"),
      lastname: (value) => validateName(value, "Lastname"),
      email: validateEmail,
      password: validatePassword,
      confirmPassword: (value, values) =>
        validateConfirmPassword(values.password, value),
    },
  });

  const handleSubmit = async (values: Register) => {
    try {
      await registerCall(values);
      showInfoMessage("Registration successful");
      navigate("/login");
    } catch (error) {
      showErrorMessage("Registration failed");
      console.error(error);
    }
  };

  return (
    <Container size={420} my={40}>
      <Title ta="center">Nice to see you!</Title>
      <Text c="dimmed" size="sm" ta="center" mt={5}>
        You already have an account?{" "}
        <Anchor size="sm" onClick={() => navigate("/login")}>
          Log in
        </Anchor>
      </Text>

      <Paper withBorder shadow="md" p={30} mt={30} radius="md">
        <form onSubmit={form.onSubmit(handleSubmit)}>
          <TextInput
            label="Username"
            placeholder="Username"
            onChange={(event) =>
              form.setFieldValue("username", event.currentTarget.value)
            }
            error={form.errors.username}
            required
          />
          <TextInput
            label="Email"
            placeholder="Your email"
            onChange={(event) =>
              form.setFieldValue("email", event.currentTarget.value)
            }
            error={form.errors.email}
            required
          />
          <TextInput
            label="Firstname"
            placeholder="Your firstname"
            onChange={(event) =>
              form.setFieldValue("firstname", event.currentTarget.value)
            }
            error={form.errors.firstname}
            required
          />
          <TextInput
            label="Lastname"
            placeholder="Your lastname"
            onChange={(event) =>
              form.setFieldValue("lastname", event.currentTarget.value)
            }
            error={form.errors.lastname}
            required
          />
          <PasswordInput
            label="Password"
            placeholder="Password"
            onChange={(event) =>
              form.setFieldValue("password", event.currentTarget.value)
            }
            error={form.errors.password}
            required
            mt="md"
          />
          <PasswordInput
            label="Repeat password"
            placeholder="Repeat password"
            onChange={(event) =>
              form.setFieldValue("confirmPassword", event.currentTarget.value)
            }
            error={form.errors.confirmPassword}
            required
          />
          <Button fullWidth mt="xl" type="submit">
            Sign up
          </Button>
        </form>
      </Paper>
    </Container>
  );
}
