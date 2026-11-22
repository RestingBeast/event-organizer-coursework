import DateFilter from "@/components/DateFilter";
import EventCard from "@/components/EventsCard";
import SearchBox from "@/components/SearchBox";
import { useEvents } from "@/context/EventsContext";
import { toYMD } from "@/lib/dates";
import { router } from "expo-router";
import { useState } from "react";
import { FlatList, Pressable, StyleSheet, Text, View } from "react-native";

export default function Index() {
  const { events, loading, error, reload } = useEvents();
  const [query, setQuery] = useState("");
  const [date, setDate] = useState<Date | null>(null);

  const dateYMD = date ? toYMD(date) : null;
  const q = query.trim().toLowerCase();
  const filtered = events.filter(
    (e) =>
      (!q || e.name.toLowerCase().includes(q)) &&
      (!dateYMD || e.date === dateYMD),
  );

  return (
    <View style={styles.container}>
      <SearchBox query={query} setQuery={setQuery} />
      <DateFilter date={date} setDate={setDate} />
      {loading && events.length === 0 ? (
        <Text>Loading...</Text>
      ) : (
        <FlatList
          data={filtered}
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
              {filtered.length} {filtered.length === 1 ? "event" : "events"}
            </Text>
          }
          ListEmptyComponent={
            <Text>
              {q || date
                ? `No events match "${query.trim()}"`
                : "No events available yet."}
            </Text>
          }
        />
      )}
      {error && events.length === 0 && (
        <>
          <Text>{error}</Text>
          <Pressable onPress={reload}>Retry</Pressable>
        </>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
});
