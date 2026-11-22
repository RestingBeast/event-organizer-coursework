import { formatDate } from "@/lib/dates";
import MaterialCommunityIcons from "@expo/vector-icons/MaterialCommunityIcons";
import { DateTimePickerAndroid } from "@react-native-community/datetimepicker";
import type { Dispatch, SetStateAction } from "react";
import { Pressable, StyleSheet, Text, View } from "react-native";

type Props = {
  date: Date | null;
  setDate: Dispatch<SetStateAction<Date | null>>;
};

export default function DateFilter({ date, setDate }: Props) {
  function openPicker() {
    DateTimePickerAndroid.open({
      value: date ?? new Date(),
      mode: "date",
      design: "material",
      title: "Search by date",
      onValueChange: (_event, picked) => setDate(picked),
    });
  }
  return (
    <View style={styles.row}>
      <Pressable
        onPress={openPicker}
        style={[styles.chip, date && styles.chipActive]}
      >
        <MaterialCommunityIcons
          name="calendar"
          size={18}
          color={date ? "#2F6F6A" : "#75614F"}
        />
        <Text style={[styles.label, date && styles.labelActive]}>
          {date ? formatDate(date) : "Any date"}
        </Text>
      </Pressable>
      {date && (
        <Pressable onPress={() => setDate(null)} hitSlop={8}>
          <MaterialCommunityIcons
            name="close-circle"
            size={18}
            color="#75614F"
          />
        </Pressable>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: "row",
    alignItems: "center",
    gap: 8,
    marginHorizontal: 20,
    marginTop: 8,
  },
  chip: {
    flexDirection: "row",
    alignItems: "center",
    gap: 6,
    height: 36,
    paddingHorizontal: 12,
    borderRadius: 18,
    borderWidth: 1,
    borderColor: "#E2CBB8",
    backgroundColor: "#FFFFFF",
  },
  chipActive: { backgroundColor: "#D6E8E5", borderColor: "#2F6F6A" },
  label: { fontSize: 14, color: "#75614F" },
  labelActive: { color: "#2F6F6A", fontWeight: "600" },
});
