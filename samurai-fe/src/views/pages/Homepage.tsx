import { Box, Button, Flex, Group, Text, Title } from "@mantine/core";
import { IconBrandGithub } from "@tabler/icons-react";
import { useNavigate } from "react-router-dom";

export default function Homepage() {
  const navigate = useNavigate();
  return (
    <Flex justify="center" align="center" h="80vh">
      <Box maw={900}>
        <Title
          order={1}
          fz={62}
          fw={900}
          c="light-dark(var(--mantine-color-black), var(--mantine-color-white))"
        >
          An{" "}
          <Text
            component="span"
            variant="gradient"
            gradient={{ from: "violet", to: "blue" }}
            inherit
          >
            AI-driven
          </Text>{" "}
          document search and retrieval system
        </Title>

        <Text fz={24} mt="xl" c="dimmed">
          Easily locate the information you need with SamurAI. AI technology
          helps you quickly find data within your documents and answers your
          questions about them.
        </Text>

        <Group mt="xl">
          <Button
            size="lg"
            variant="gradient"
            onClick={() => navigate("/conversations/new")}
            gradient={{ from: "violet", to: "blue" }}
          >
            Get started
          </Button>

          <Button
            component="a"
            href="https://github.com/ochnios/samurai"
            target="_blank"
            size="lg"
            variant="default"
            leftSection={<IconBrandGithub size={20} />}
          >
            GitHub
          </Button>
        </Group>
      </Box>
    </Flex>
  );
}
