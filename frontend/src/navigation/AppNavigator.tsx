import React, { useEffect, useState } from 'react';
import { View, Text, ActivityIndicator, TouchableOpacity } from 'react-native';
import authService from '../core/api/authService';

const AppNavigator: React.FC = () => {
  const [backendStatus, setBackendStatus] = useState<string>('checking...');
  const [isConnected, setIsConnected] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    checkBackendConnection();
  }, []);

  const checkBackendConnection = async () => {
    setIsLoading(true);
    try {
      const response = await authService.health();
      setBackendStatus(`✅ Connected: ${response.status}`);
      setIsConnected(true);
    } catch (error: any) {
      setBackendStatus(`❌ Error: ${error.message}`);
      setIsConnected(false);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center', padding: 20 }}>
      <Text style={{ fontSize: 24, fontWeight: 'bold', marginBottom: 20 }}>
        🌱 VeganApp
      </Text>
      
      {isLoading ? (
        <ActivityIndicator size="large" color="#1976d2" />
      ) : (
        <>
          <View style={{ 
            backgroundColor: isConnected ? '#e8f5e9' : '#ffebee',
            padding: 15,
            borderRadius: 8,
            marginBottom: 20,
            minWidth: 250
          }}>
            <Text style={{ 
              fontSize: 14, 
              color: isConnected ? '#2e7d32' : '#c62828',
              textAlign: 'center',
              fontWeight: '500'
            }}>
              Backend Status
            </Text>
            <Text style={{ 
              fontSize: 12, 
              color: isConnected ? '#2e7d32' : '#c62828',
              textAlign: 'center',
              marginTop: 8
            }}>
              {backendStatus}
            </Text>
          </View>

          <TouchableOpacity 
            onPress={checkBackendConnection}
            style={{
              backgroundColor: '#1976d2',
              paddingHorizontal: 20,
              paddingVertical: 12,
              borderRadius: 6,
            }}
          >
            <Text style={{ color: '#fff', fontSize: 14, fontWeight: '600' }}>
              Retry Connection
            </Text>
          </TouchableOpacity>
        </>
      )}

      <View style={{ marginTop: 30, alignItems: 'center' }}>
        <Text style={{ fontSize: 12, color: '#666', marginBottom: 15 }}>
          Backend: http://localhost:8080/api
        </Text>
        <Text style={{ fontSize: 10, color: '#999', textAlign: 'center' }}>
          Frontend & Backend Integration Ready
        </Text>
      </View>
    </View>
  );
};

export default AppNavigator;
