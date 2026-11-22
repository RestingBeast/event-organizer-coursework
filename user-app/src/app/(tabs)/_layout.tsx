import MaterialCommunityIcons from "@expo/vector-icons/MaterialCommunityIcons";
import { Tabs } from "expo-router/js-tabs";

const BACKGROUND = "#FFF1E6";
const TEXT = "#2B211B";

export default function TabsLayout() {
  return (
    <Tabs
      screenOptions={{
        sceneStyle: { backgroundColor: BACKGROUND },
        headerStyle: { backgroundColor: BACKGROUND },
        headerTintColor: TEXT,
        headerShadowVisible: false,
      }}
    >
      <Tabs.Screen
        name="index"
        options={{
          title: "Events",
          tabBarIcon: ({ color, size }) => (
            <MaterialCommunityIcons
              name="calendar"
              color={color as string}
              size={size}
            />
          ),
        }}
      />
      <Tabs.Screen
        name="favourites"
        options={{
          title: "Favourites",
          tabBarIcon: ({ color, size }) => (
            <MaterialCommunityIcons
              name="star"
              color={color as string}
              size={size}
            />
          ),
        }}
      />
    </Tabs>
  );
}
