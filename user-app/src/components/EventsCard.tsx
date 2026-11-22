import type { Event } from "@/types";
import { Pressable, Text } from "react-native";

type Props = {
  event: Event;
  onPress: () => void;
};

export default function EventCard({ event, onPress }: Props) {
  return (
    <Pressable onPress={onPress}>
      <Text numberOfLines={1}>{event.name}</Text>
      <Text numberOfLines={1}>
        {event.date} · {event.location}
      </Text>
      <Text numberOfLines={1}>by {event.organizer}</Text>
    </Pressable>
  );
}
