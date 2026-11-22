import { useLocalSearchParams } from "expo-router";
import { Text } from "react-native";

export default function EventDetails() {
  const { id } = useLocalSearchParams<{ id: string }>();
  return <Text>Event {id}</Text>;
}
