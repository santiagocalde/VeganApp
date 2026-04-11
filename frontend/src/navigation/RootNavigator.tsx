import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { useAuthStore } from '../core/store/authStore';
import AppNavigator from './AppNavigator';
import LoginScreen from '../features/auth/LoginScreen';

const Stack = createNativeStackNavigator();

const RootNavigator: React.FC = () => {
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);

  return (
    <NavigationContainer>
      <Stack.Navigator
        screenOptions={{
          headerShown: false,
          animationEnabled: true,
        }}
      >
        {!isAuthenticated ? (
          // Auth Stack
          <Stack.Group
            screenOptions={{
              animationEnabled: false,
            }}
          >
            <Stack.Screen
              name="Login"
              component={LoginScreen}
              options={{
                title: 'Inicio de Sesión',
              }}
            />
          </Stack.Group>
        ) : (
          // App Stack
          <Stack.Group>
            <Stack.Screen
              name="AppStack"
              component={AppNavigator}
              options={{
                title: 'VeganApp',
              }}
            />
          </Stack.Group>
        )}
      </Stack.Navigator>
    </NavigationContainer>
  );
};

export default RootNavigator;
