import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  FlatList,
  TouchableOpacity,
  StyleSheet,
  ActivityIndicator,
  Alert,
  CheckBox,
} from 'react-native';
import shoppingListService, { ShoppingItem } from '../../core/api/shoppingService';

const ShoppingListScreen = () => {
  const [items, setItems] = useState<ShoppingItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    loadShoppingList();
  }, []);

  const loadShoppingList = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await shoppingListService.getItems();
      setItems(data);
    } catch (err: any) {
      setError(err.message || 'Error loading shopping list');
    } finally {
      setLoading(false);
    }
  };

  const toggleItem = async (item: ShoppingItem) => {
    try {
      await shoppingListService.updateItem(item.id, {
        isChecked: !item.isChecked,
      });
      setItems(
        items.map((i) =>
          i.id === item.id ? { ...i, isChecked: !i.isChecked } : i
        )
      );
    } catch (err) {
      Alert.alert('Error', 'No se pudo actualizar el elemento');
    }
  };

  const deleteItem = async (id: string) => {
    try {
      await shoppingListService.deleteItem(id);
      setItems(items.filter((i) => i.id !== id));
    } catch (err) {
      Alert.alert('Error', 'No se pudo eliminar el elemento');
    }
  };

  const clearChecked = async () => {
    try {
      await shoppingListService.clearChecked();
      loadShoppingList();
    } catch (err) {
      Alert.alert('Error', 'No se pudieron eliminar los items marcados');
    }
  };

  const renderItem = ({ item }: { item: ShoppingItem }) => (
    <View style={styles.itemContainer}>
      <CheckBox
        value={item.isChecked}
        onValueChange={() => toggleItem(item)}
        style={styles.checkbox}
      />
      <View style={styles.itemInfo}>
        <Text
          style={[
            styles.itemName,
            item.isChecked && styles.itemNameChecked,
          ]}
        >
          {item.name}
        </Text>
        <Text style={styles.itemMeta}>
          {item.quantity} {item.unit} • {item.category}
        </Text>
      </View>
      <TouchableOpacity
        onPress={() => deleteItem(item.id)}
        style={styles.deleteButton}
      >
        <Text style={styles.deleteText}>✕</Text>
      </TouchableOpacity>
    </View>
  );

  if (loading) {
    return (
      <View style={styles.centerContainer}>
        <ActivityIndicator size="large" color="#2e7d32" />
      </View>
    );
  }

  const checkedCount = items.filter((i) => i.isChecked).length;

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Lista de Compras</Text>
        <Text style={styles.count}>
          {items.length} items ({checkedCount} ✓)
        </Text>
      </View>

      {checkedCount > 0 && (
        <TouchableOpacity
          style={styles.clearButton}
          onPress={clearChecked}
        >
          <Text style={styles.clearButtonText}>
            Limpiar {checkedCount} marcados
          </Text>
        </TouchableOpacity>
      )}

      {items.length === 0 ? (
        <View style={styles.emptyContainer}>
          <Text style={styles.emptyText}>Lista vacía</Text>
        </View>
      ) : (
        <FlatList
          data={items}
          renderItem={renderItem}
          keyExtractor={(item) => item.id}
          contentContainerStyle={styles.listContent}
        />
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  centerContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  header: {
    backgroundColor: '#fff',
    padding: 15,
    borderBottomWidth: 1,
    borderBottomColor: '#ddd',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 5,
  },
  count: {
    fontSize: 12,
    color: '#999',
  },
  clearButton: {
    backgroundColor: '#2e7d32',
    margin: 10,
    padding: 12,
    borderRadius: 6,
    alignItems: 'center',
  },
  clearButtonText: {
    color: '#fff',
    fontWeight: '600',
  },
  listContent: {
    padding: 10,
  },
  itemContainer: {
    flexDirection: 'row',
    backgroundColor: '#fff',
    borderRadius: 6,
    padding: 12,
    marginBottom: 8,
    alignItems: 'center',
    elevation: 1,
  },
  checkbox: {
    marginRight: 12,
  },
  itemInfo: {
    flex: 1,
  },
  itemName: {
    fontSize: 14,
    fontWeight: '500',
    color: '#333',
  },
  itemNameChecked: {
    color: '#999',
    textDecorationLine: 'line-through',
  },
  itemMeta: {
    fontSize: 12,
    color: '#999',
    marginTop: 4,
  },
  deleteButton: {
    padding: 8,
  },
  deleteText: {
    fontSize: 20,
    color: '#c62828',
  },
  emptyContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  emptyText: {
    color: '#999',
    fontSize: 14,
  },
});

export default ShoppingListScreen;
