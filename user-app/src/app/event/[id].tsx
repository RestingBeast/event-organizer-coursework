import { useEvents } from "@/context/EventsContext";
import { useFavourites } from "@/context/FavouritesContext";
import MaterialCommunityIcons from "@expo/vector-icons/MaterialCommunityIcons";
import { Stack, useLocalSearchParams } from "expo-router";
import { Pressable, ScrollView, Text, View } from "react-native";

export default function EventDetails() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const { events, loading } = useEvents();
  const { isFavourite, toggle } = useFavourites();

  const event = events.find((e) => e.id === Number(id));
  if (!event) return <Text>{loading ? "Loading..." : "Event not found."}</Text>;
  const fav = isFavourite(event.id);
  const sessions = [...(event.sessions ?? [])].sort((a, b) =>
    a.time.localeCompare(b.time),
  );
  return (
    <>
      <Stack.Screen
        options={{
          title: "Event Details",
        }}
      />
      <ScrollView contentContainerStyle={{ padding: 20 }}>
        <Text>Name: {event.name}</Text>
        <Pressable onPress={() => toggle(event.id)} hitSlop={10}>
          <MaterialCommunityIcons
            name={fav ? "star" : "star-outline"}
            size={24}
            color="#2F6F6A"
          />
        </Pressable>
        <Text>Location: {event.location}</Text>
        <Text>Organizer: {event.organizer}</Text>
        <Text>Type: {event.type}</Text>
        <Text>Status: {event.status}</Text>
        {event.description && <Text>Description: {event.description}</Text>}
        {event.email && <Text>Email: {event.email}</Text>}
        {event.phone && <Text>Phone: {event.phone}</Text>}
        {sessions.length === 0 ? (
          <Text>No sessions</Text>
        ) : (
          sessions.map((s) => (
            <View key={s.id}>
              <Text>{s.name}</Text>
              <Text>{s.time.slice(0, 5)}</Text>
            </View>
          ))
        )}
      </ScrollView>
    </>
  );
}
