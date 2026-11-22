import { useFavourites } from "@/context/FavouritesContext";
import { MONTHS } from "@/lib/dates";
import type { Event } from "@/types";
import MaterialCommunityIcons from "@expo/vector-icons/MaterialCommunityIcons";
import { Pressable, StyleSheet, Text, View } from "react-native";

// Colours from the admin app's colors.xml
const COLORS = {
  surface: "#FFFFFF",
  background: "#FFF1E6",
  outline: "#E2CBB8",
  accent: "#2F6F6A",
  textPrimary: "#2B211B",
  textSecondary: "#75614F",
};

const STATUS_COLORS: Record<string, string> = {
  Upcoming: "#2457A6",
  Completed: "#75614F",
  Cancelled: "#B3261E",
};

type Props = {
  event: Event;
  onPress: () => void;
};

export default function EventCard({ event, onPress }: Props) {
  const { isFavourite, toggle, loaded } = useFavourites();
  const fav = isFavourite(event.id);

  // "2026-10-19" → day "19", month "Oct"
  const [, month, day] = event.date.split("-");
  const monthLabel = MONTHS[Number(month) - 1] ?? "";

  const sessionCount = event.sessions?.length ?? 0;
  const statusColor = STATUS_COLORS[event.status] ?? COLORS.textSecondary;

  return (
    <Pressable
      onPress={onPress}
      android_ripple={{ color: COLORS.outline }}
      style={({ pressed }) => [styles.card, pressed && styles.pressed]}
    >
      <View style={styles.top}>
        <View style={styles.dateBlock}>
          <Text style={styles.day}>{Number(day)}</Text>
          <Text style={styles.month}>{monthLabel}</Text>
        </View>

        <View style={styles.info}>
          <Text style={styles.name} numberOfLines={1}>
            {event.name}
          </Text>
          <Text style={styles.organizer} numberOfLines={1}>
            by {event.organizer}
          </Text>

          <View style={styles.pills}>
            <Text
              style={[
                styles.pill,
                { borderColor: COLORS.accent, color: COLORS.accent },
              ]}
            >
              {event.type}
            </Text>
            <Text
              style={[
                styles.pill,
                { borderColor: statusColor, color: statusColor },
              ]}
            >
              {event.status}
            </Text>
          </View>

          {sessionCount > 0 && (
            <View style={styles.sessions}>
              <MaterialCommunityIcons
                name="clock-outline"
                size={14}
                color={COLORS.textSecondary}
              />
              <Text style={styles.secondary}>
                {sessionCount} {sessionCount === 1 ? "session" : "sessions"}
              </Text>
            </View>
          )}
        </View>

        {loaded && (
          <Pressable
            onPress={() => toggle(event.id)}
            hitSlop={10}
            style={styles.star}
          >
            <MaterialCommunityIcons
              name={fav ? "star" : "star-outline"}
              size={26}
              color={fav ? COLORS.accent : COLORS.textSecondary}
            />
          </Pressable>
        )}
      </View>

      <View style={styles.divider} />

      <View style={styles.footer}>
        <MaterialCommunityIcons
          name="map-marker-outline"
          size={16}
          color={COLORS.textSecondary}
        />
        <Text style={[styles.secondary, styles.location]} numberOfLines={1}>
          {event.location}
        </Text>
      </View>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: COLORS.surface,
    borderColor: COLORS.outline,
    borderWidth: 1,
    borderRadius: 16,
    marginBottom: 12,
    overflow: "hidden",
  },
  pressed: { opacity: 0.85 },
  top: {
    flexDirection: "row",
    padding: 16,
    gap: 16,
  },
  dateBlock: {
    width: 56,
    height: 56,
    borderRadius: 12,
    backgroundColor: COLORS.background,
    alignItems: "center",
    justifyContent: "center",
  },
  day: {
    fontSize: 22,
    fontWeight: "700",
    color: COLORS.textPrimary,
    lineHeight: 26,
  },
  month: {
    fontSize: 11,
    fontWeight: "600",
    color: COLORS.accent,
    textTransform: "uppercase",
    letterSpacing: 0.5,
  },
  info: { flex: 1 },
  name: {
    fontSize: 16,
    fontWeight: "600",
    color: COLORS.textPrimary,
  },
  organizer: {
    fontSize: 13,
    color: COLORS.textSecondary,
    marginTop: 2,
  },
  pills: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 8,
    marginTop: 8,
  },
  pill: {
    fontSize: 11,
    fontWeight: "500",
    borderWidth: 1,
    borderRadius: 100,
    paddingHorizontal: 10,
    paddingVertical: 2,
    overflow: "hidden",
  },
  sessions: {
    flexDirection: "row",
    alignItems: "center",
    gap: 4,
    marginTop: 8,
  },
  secondary: {
    fontSize: 13,
    color: COLORS.textSecondary,
  },
  star: { alignSelf: "flex-start" },
  divider: {
    height: 1,
    backgroundColor: COLORS.outline,
  },
  footer: {
    flexDirection: "row",
    alignItems: "center",
    gap: 6,
    paddingHorizontal: 16,
    paddingVertical: 10,
  },
  location: { flex: 1 },
});
