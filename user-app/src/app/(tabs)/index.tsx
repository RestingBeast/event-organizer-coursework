import EventCard from "@/components/EventsCard";
import { useEvents } from "@/context/EventsContext";
import { router } from "expo-router";
import { FlatList, StyleSheet, Text, View } from "react-native";

export default function Index() {
  const { events, loading, error, reload } = useEvents();
  return (
    <View style={styles.container}>
      {loading ? (
        "Loading..."
      ) : (
        <FlatList
          data={events}
          keyExtractor={(e) => String(e.id)}
          renderItem={({ item }) => (
            <EventCard
              event={item}
              onPress={() => router.push(`/event/${item.id}`)}
            />
          )}
          refreshing={loading}
          onRefresh={reload}
          contentContainerStyle={{ padding: 20 }}
          ListHeaderComponent={
            <Text>
              {events.length} {events.length === 1 ? "event" : "events"}
            </Text>
          }
          ListEmptyComponent={<Text>No events available yet.</Text>}
        />
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
});
