import SessionCard from "@/components/SessionCard";
import { useEvents } from "@/context/EventsContext";
import { useFavourites } from "@/context/FavouritesContext";
import { MONTHS } from "@/lib/dates";
import MaterialCommunityIcons from "@expo/vector-icons/MaterialCommunityIcons";
import { Stack, useLocalSearchParams } from "expo-router";
import type { ComponentProps } from "react";
import { Pressable, ScrollView, StyleSheet, Text, View } from "react-native";

const STATUS_COLORS: Record<string, string> = {
  Upcoming: "#2457A6",
  Completed: "#75614F",
  Cancelled: "#B3261E",
};

type IconName = ComponentProps<typeof MaterialCommunityIcons>["name"];

function InfoRow({ icon, text }: { icon: IconName; text: string }) {
  return (
    <View style={styles.infoRow}>
      <MaterialCommunityIcons name={icon} size={18} color="#75614F" />
      <Text style={styles.infoText}>{text}</Text>
    </View>
  );
}

export default function EventDetails() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const { events, loading } = useEvents();
  const { isFavourite, toggle } = useFavourites();

  const event = events.find((e) => e.id === Number(id));
  if (!event)
    return (
      <View style={styles.center}>
        <Text style={styles.muted}>{loading ? "Loading..." : "Event not found."}</Text>
      </View>
    );

  const fav = isFavourite(event.id);
  const sessions = [...(event.sessions ?? [])].sort((a, b) =>
    a.time.localeCompare(b.time),
  );
  const [year, month, day] = event.date.split("-");
  const dateLabel = `${Number(day)} ${MONTHS[Number(month) - 1]} ${year}`;
  const statusColor = STATUS_COLORS[event.status] ?? "#75614F";

  return (
    <>
      <Stack.Screen options={{ title: "Event Details" }} />
      <ScrollView contentContainerStyle={styles.container}>
        <View style={styles.titleRow}>
          <Text style={styles.title}>{event.name}</Text>
          <Pressable onPress={() => toggle(event.id)} hitSlop={10}>
            <MaterialCommunityIcons
              name={fav ? "star" : "star-outline"}
              size={28}
              color={fav ? "#2F6F6A" : "#75614F"}
            />
          </Pressable>
        </View>
        <Text style={styles.muted}>by {event.organizer}</Text>

        <View style={styles.pills}>
          <Text style={[styles.pill, { color: "#2F6F6A", borderColor: "#2F6F6A" }]}>
            {event.type}
          </Text>
          <Text style={[styles.pill, { color: statusColor, borderColor: statusColor }]}>
            {event.status}
          </Text>
        </View>

        <View style={styles.infoCard}>
          <InfoRow icon="calendar" text={dateLabel} />
          <InfoRow icon="map-marker-outline" text={event.location} />
          {event.email && <InfoRow icon="email-outline" text={event.email} />}
          {event.phone && <InfoRow icon="phone-outline" text={event.phone} />}
        </View>

        {event.description && (
          <>
            <Text style={styles.heading}>About</Text>
            <Text style={styles.body}>{event.description}</Text>
          </>
        )}

        <Text style={styles.heading}>Sessions ({sessions.length})</Text>
        {sessions.length === 0 ? (
          <Text style={styles.muted}>No sessions scheduled.</Text>
        ) : (
          sessions.map((s) => <SessionCard key={s.id} session={s} />)
        )}
      </ScrollView>
    </>
  );
}

const styles = StyleSheet.create({
  container: { padding: 20, paddingBottom: 40 },
  center: { flex: 1, alignItems: "center", justifyContent: "center" },
  titleRow: { flexDirection: "row", alignItems: "flex-start", gap: 12 },
  title: { flex: 1, fontSize: 24, fontWeight: "700", color: "#2B211B" },
  muted: { fontSize: 14, color: "#75614F", marginTop: 4 },
  pills: { flexDirection: "row", gap: 8, marginTop: 12 },
  pill: {
    fontSize: 12,
    fontWeight: "500",
    borderWidth: 1,
    borderRadius: 100,
    paddingHorizontal: 10,
    paddingVertical: 2,
    overflow: "hidden",
  },
  infoCard: {
    backgroundColor: "#FFFFFF",
    borderColor: "#E2CBB8",
    borderWidth: 1,
    borderRadius: 16,
    padding: 16,
    marginTop: 20,
    gap: 12,
  },
  infoRow: { flexDirection: "row", alignItems: "center", gap: 10 },
  infoText: { flex: 1, fontSize: 15, color: "#2B211B" },
  heading: {
    fontSize: 18,
    fontWeight: "600",
    color: "#2B211B",
    marginTop: 24,
    marginBottom: 12,
  },
  body: { fontSize: 15, color: "#2B211B", lineHeight: 22 },
});
