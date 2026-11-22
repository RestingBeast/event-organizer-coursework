import { EventsProvider } from "@/context/EventsContext";
import { Stack } from "expo-router";
import { StatusBar } from "expo-status-bar";

const BACKGROUND = "#FFF1E6";
const TEXT = "#2B211B";

export default function RootLayout() {
  return (
    <EventsProvider>
      <Stack
        screenOptions={{
          contentStyle: { backgroundColor: BACKGROUND },
          headerStyle: { backgroundColor: BACKGROUND },
          headerTintColor: TEXT,
          headerShadowVisible: false,
        }}
      >
        <Stack.Screen name="(tabs)" options={{ headerShown: false }} />
      </Stack>
      <StatusBar style="dark" />
    </EventsProvider>
  );
}
