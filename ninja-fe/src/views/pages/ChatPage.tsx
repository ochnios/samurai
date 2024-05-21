import { Grid, Text, Title } from "@mantine/core";

export default function ChatPage() {
  return (
    <Grid>
      <Grid.Col span={{ base: 12, md: 4, lg: 4 }}>
        <Title order={4}>Conversations</Title>
      </Grid.Col>
      <Grid.Col span={{ base: 12, md: 8, lg: 8 }}>
        <Title order={5}>
          <Text fz="md">Messages here...</Text>
        </Title>
      </Grid.Col>
    </Grid>
  );
}
