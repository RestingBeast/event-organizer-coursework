import type { Session } from "@/types";
import { StyleSheet, Text, View } from "react-native";

export default function SessionCard({ session }: { session: Session }) {
  const meta = [session.room, session.speaker].filter(Boolean).join(" · ");

  return (
    <View style={styles.card}>
      <View style={styles.row}>
        <View style={styles.timeBlock}>
          <Text style={styles.time}>{session.time.slice(0, 5)}</Text>
        </View>
        <View style={styles.info}>
          <Text style={styles.name}>{session.name}</Text>
          {meta !== "" && <Text style={styles.meta}>{meta}</Text>}
          {session.type && <Text style={styles.pill}>{session.type}</Text>}
        </View>
      </View>
      {session.description && (
        <Text style={styles.desc}>{session.description}</Text>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: "#FFFFFF",
    borderColor: "#E2CBB8",
    borderWidth: 1,
    borderRadius: 16,
    padding: 16,
    marginBottom: 12,
  },
  row: { flexDirection: "row", alignItems: "center", gap: 16 },
  timeBlock: {
    width: 56,
    height: 56,
    borderRadius: 12,
    backgroundColor: "#FFF1E6",
    alignItems: "center",
    justifyContent: "center",
  },
  time: { fontSize: 14, fontWeight: "700", color: "#2B211B" },
  info: { flex: 1 },
  name: { fontSize: 16, fontWeight: "600", color: "#2B211B" },
  meta: { fontSize: 13, color: "#75614F", marginTop: 4 },
  pill: {
    alignSelf: "flex-start",
    marginTop: 8,
    fontSize: 11,
    fontWeight: "500",
    color: "#2F6F6A",
    borderColor: "#2F6F6A",
    borderWidth: 1,
    borderRadius: 100,
    paddingHorizontal: 10,
    paddingVertical: 2,
    overflow: "hidden",
  },
  desc: { fontSize: 14, color: "#2B211B", marginTop: 16, lineHeight: 20 },
});
