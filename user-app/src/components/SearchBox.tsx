import MaterialCommunityIcons from "@expo/vector-icons/MaterialCommunityIcons";
import type { Dispatch, SetStateAction } from "react";
import { Pressable, StyleSheet, TextInput, View } from "react-native";

type Props = {
  query: string;
  setQuery: Dispatch<SetStateAction<string>>;
};

export default function SearchBox({ query, setQuery }: Props) {
  return (
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
});
