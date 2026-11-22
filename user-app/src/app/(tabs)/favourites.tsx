import EventCard from "@/components/EventsCard";
import { useEvents } from "@/context/EventsContext";
import { useFavourites } from "@/context/FavouritesContext";
import { router } from "expo-router";
import { FlatList, StyleSheet, Text, View } from "react-native";

export default function Favourites() {
  const { events } = useEvents();
  const { ids } = useFavourites();
  const favourites = events.filter((e) => ids.has(e.id));

  return (
    <View style={styles.container}>
      <FlatList
        data={favourites}
        renderItem={({ item }) => (
          <EventCard
            event={item}
            onPress={() => router.push(`/event/${item.id}`)}
          />
        )}
        contentContainerStyle={{ padding: 20 }}
        ListHeaderComponent={
          <Text>
            {favourites.length} {favourites.length === 1 ? "event" : "events"}
          </Text>
        }
        ListEmptyComponent={<Text>No favourites yet.</Text>}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
});
