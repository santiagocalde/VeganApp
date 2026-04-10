import React from 'react';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { Ionicons } from '@expo/vector-icons';
import HomeScreen from '../features/home/HomeScreen';
import RecipeListScreen from '../features/recipes/RecipeListScreen';
import ShoppingListScreen from '../features/shopping/ShoppingListScreen';
import ProfileScreen from '../features/profile/ProfileScreen';
import { useAuthStore } from '../core/store/authStore';
import { TouchableOpacity, Text } from 'react-native';

const Stack = createNativeStackNavigator();
const Tab = createBottomTabNavigator();

// Home Stack
const HomeStack = () => (
  <Stack.Navigator
    screenOptions={{
      headerShown: true,
      headerStyle: { backgroundColor: '#2e7d32' },
      headerTintColor: '#fff',
      headerTitleStyle: { fontWeight: '600' },
    }}
  >
    <Stack.Screen
      name="HomeScreen"
      component={HomeScreen}
      options={{ title: 'Inicio' }}
    />
  </Stack.Navigator>
);

// Recipes Stack
const RecipesStack = () => (
  <Stack.Navigator
    screenOptions={{
      headerShown: true,
      headerStyle: { backgroundColor: '#2e7d32' },
      headerTintColor: '#fff',
      headerTitleStyle: { fontWeight: '600' },
    }}
  >
    <Stack.Screen
      name="RecipesScreen"
      component={RecipeListScreen}
      options={{ title: 'Recetas' }}
    />
  </Stack.Navigator>
);

// Shopping Stack
const ShoppingStack = () => (
  <Stack.Navigator
    screenOptions={{
      headerShown: true,
      headerStyle: { backgroundColor: '#2e7d32' },
      headerTintColor: '#fff',
      headerTitleStyle: { fontWeight: '600' },
    }}
  >
    <Stack.Screen
      name="ShoppingScreen"
      component={ShoppingListScreen}
      options={{ title: 'Lista de Compras' }}
    />
  </Stack.Navigator>
);

// Profile Stack
const ProfileStack = () => (
  <Stack.Navigator
    screenOptions={{
      headerShown: true,
      headerStyle: { backgroundColor: '#2e7d32' },
      headerTintColor: '#fff',
      headerTitleStyle: { fontWeight: '600' },
    }}
  >
    <Stack.Screen
      name="ProfileScreen"
      component={ProfileScreen}
      options={{
        title: 'Perfil',
        headerRight: () => <LogoutButton />,
      }}
    />
  </Stack.Navigator>
);

// Logout Button
const LogoutButton = () => {
  const logout = useAuthStore((state) => state.logout);

  return (
    <TouchableOpacity
      onPress={logout}
      style={{ marginRight: 15, padding: 8 }}
    >
      <Text style={{ color: '#fff', fontSize: 14, fontWeight: '600' }}>
        Salir
      </Text>
    </TouchableOpacity>
  );
};

// Bottom Tab Navigator
const AppStackNavigator = () => (
  <Tab.Navigator
    screenOptions={({ route }) => ({
      headerShown: false,
      tabBarActiveTintColor: '#2e7d32',
      tabBarInactiveTintColor: '#999',
      tabBarIcon: ({ focused, color, size }) => {
        let iconName: keyof typeof Ionicons.glyphMap;

        switch (route.name) {
          case 'Home':
            iconName = focused ? 'home' : 'home-outline';
            break;
          case 'Recipes':
            iconName = focused ? 'restaurant' : 'restaurant-outline';
            break;
          case 'Shopping':
            iconName = focused ? 'cart' : 'cart-outline';
            break;
          case 'Profile':
            iconName = focused ? 'person' : 'person-outline';
            break;
          default:
            iconName = 'ellipse';
        }

        return <Ionicons name={iconName} size={size} color={color} />;
      },
    })}
  >
    <Tab.Screen
      name="Home"
      component={HomeStack}
      options={{
        title: 'Inicio',
        tabBarLabel: 'Inicio',
      }}
    />
    <Tab.Screen
      name="Recipes"
      component={RecipesStack}
      options={{
        title: 'Recetas',
        tabBarLabel: 'Recetas',
      }}
    />
    <Tab.Screen
      name="Shopping"
      component={ShoppingStack}
      options={{
        title: 'Compras',
        tabBarLabel: 'Compras',
      }}
    />
    <Tab.Screen
      name="Profile"
      component={ProfileStack}
      options={{
        title: 'Perfil',
        tabBarLabel: 'Perfil',
      }}
    />
  </Tab.Navigator>
);

export default AppStackNavigator;
