import React from 'react';
import { Text, View } from 'react-native';

const AppNavigator: React.FC = () => {
  return (
    <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
      <Text style={{ fontSize: 24, fontWeight: 'bold' }}>
        🌱 VeganApp
      </Text>
      <Text style={{ fontSize: 16, marginTop: 10 }}>
        Backend connected ✅
      </Text>
    </View>
  );
};

export default AppNavigator;
