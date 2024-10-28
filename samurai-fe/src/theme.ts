import { createTheme, DEFAULT_THEME, mergeMantineTheme } from "@mantine/core";

const themeOverride = createTheme({
  primaryColor: "violet",
});

export const theme = mergeMantineTheme(DEFAULT_THEME, themeOverride);
