import EventCard from "@/components/EventsCard";
import { useEvents } from "@/context/EventsContext";
import MaterialCommunityIcons from "@expo/vector-icons/MaterialCommunityIcons";
import { router } from "expo-router";
import { useState } from "react";
import {
  FlatList,
  Pressable,
  StyleSheet,
  Text,
  TextInput,
  View,
} from "react-native";

export default function Index() {
  const { events, loading, error, reload } = useEvents();
  const [query, setQuery] = useState("");

  const q = query.trim().toLowerCase();
  const filtered = q
    ? events.filter((e) => e.name.toLowerCase().includes(q))
    : events;
  return (
    <View style={styles.container}>
      <View style={styles.searchBox}>
        <MaterialCommunityIcons name="magnify" size={20} color="#75614F" />
        <TextInput
          style={styles.searchInput}
          value={query}
          onChangeText={setQuery}
          placeholder="Search events by name"
          placeholderTextColor="#75614F"
          autoCorrect={false}
          autoCapitalize="none"
          returnKeyType="search"
        />
        {query.length > 0 && (
          <Pressable onPress={() => setQuery("")} hitSlop={8}>
            <MaterialCommunityIcons
              name="close-circle"
              size={18}
              color="#75614F"
            />
          </Pressable>
        )}
      </View>
      {loading ? (
        <Text>"Loading..."</Text>
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
              {q
                ? `No events match "${query.trim()}"`
                : "No events available yet."}
            </Text>
          }
        />
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  searchBox: {
    flexDirection: "row",
    alignItems: "center",
    gap: 8,
    backgroundColor: "#FFFFFF",
    borderColor: "#E2CBB8",
    borderWidth: 1,
    borderRadius: 12,
    paddingHorizontal: 12,
    marginHorizontal: 20,
    marginTop: 12,
    height: 48,
  },
  searchInput: { flex: 1, fontSize: 15, color: "#2B211B" },
  container: {
    flex: 1,
  },
});
