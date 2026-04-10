import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  ScrollView,
  StyleSheet,
  TouchableOpacity,
  ActivityIndicator,
} from 'react-native';
import { useAuthStore } from '../../core/store/authStore';
import authService from '../../core/api/authService';

const HomeScreen = ({ navigation }: any) => {
  const user = useAuthStore((state) => state.user);
  const [backendStatus, setBackendStatus] = useState<string>('checking...');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    checkBackend();
  }, []);

  const checkBackend = async () => {
    try {
      const response = await authService.health();
      setBackendStatus(`✅ Connected: ${response.status}`);
    } catch (error) {
      setBackendStatus('❌ Backend desconectado');
    } finally {
      setLoading(false);
    }
  };

  return (
    <ScrollView style={styles.container}>
      {/* Welcome Header */}
      <View style={styles.header}>
        <Text style={styles.welcomeTitle}>🌱 ¡Bienvenido!</Text>
        {user && (
          <Text style={styles.userName}>{user.name || user.email}</Text>
        )}
      </View>

      {/* Quick Stats */}
      <View style={styles.statsContainer}>
        <View style={styles.statCard}>
          <Text style={styles.statNumber}>0</Text>
          <Text style={styles.statLabel}>Recetas</Text>
        </View>
        <View style={styles.statCard}>
          <Text style={styles.statNumber}>0</Text>
          <Text style={styles.statLabel}>Guardadas</Text>
        </View>
        <View style={styles.statCard}>
          <Text style={styles.statNumber}>0</Text>
          <Text style={styles.statLabel}>Racha</Text>
        </View>
      </View>

      {/* Backend Status */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Estado del Sistema</Text>
        {loading ? (
          <ActivityIndicator size="small" color="#2e7d32" />
        ) : (
          <View style={styles.statusBox}>
            <Text style={styles.statusText}>{backendStatus}</Text>
            <TouchableOpacity
              onPress={checkBackend}
              style={styles.retryButton}
            >
              <Text style={styles.retryText}>Verificar</Text>
            </TouchableOpacity>
          </View>
        )}
      </View>

      {/* Quick Actions */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Acciones Rápidas</Text>
        <TouchableOpacity
          style={styles.actionButton}
          onPress={() => navigation.navigate('Recipes')}
        >
          <Text style={styles.actionButtonText}>📖 Ver Recetas</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={styles.actionButton}
          onPress={() => navigation.navigate('Shopping')}
        >
          <Text style={styles.actionButtonText}>🛒 Lista de Compras</Text>
        </TouchableOpacity>
      </View>

      {/* Info Section */}
      <View style={styles.infoSection}>
        <Text style={styles.infoTitle}>Tips Veganos</Text>
        <View style={styles.tipCard}>
          <Text style={styles.tipTitle}>🥗 Proteína Vegetal</Text>
          <Text style={styles.tipText}>
            Combina legumbres, frutos secos y granos para obtener proteína completa
          </Text>
        </View>
        <View style={styles.tipCard}>
          <Text style={styles.tipTitle}>💪 Nutrientes</Text>
          <Text style={styles.tipText}>
            Asegúrate de consumir B12, hierro y omega-3 suficientes
          </Text>
        </View>
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  header: {
    backgroundColor: '#2e7d32',
    padding: 20,
    paddingTop: 10,
  },
  welcomeTitle: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#fff',
    marginBottom: 5,
  },
  userName: {
    fontSize: 16,
    color: '#e8f5e9',
  },
  statsContainer: {
    flexDirection: 'row',
    paddingHorizontal: 10,
    paddingVertical: 15,
    justifyContent: 'space-around',
  },
  statCard: {
    backgroundColor: '#fff',
    borderRadius: 10,
    padding: 15,
    alignItems: 'center',
    flex: 1,
    marginHorizontal: 5,
    elevation: 2,
  },
  statNumber: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#2e7d32',
  },
  statLabel: {
    fontSize: 12,
    color: '#666',
    marginTop: 5,
  },
  section: {
    padding: 15,
    backgroundColor: '#fff',
    marginVertical: 8,
  },
  sectionTitle: {
    fontSize: 16,
    fontWeight: '600',
    marginBottom: 12,
    color: '#333',
  },
  statusBox: {
    backgroundColor: '#e8f5e9',
    padding: 12,
    borderRadius: 8,
    borderLeftWidth: 4,
    borderLeftColor: '#2e7d32',
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  statusText: {
    flex: 1,
    fontSize: 14,
    color: '#2e7d32',
    fontWeight: '500',
  },
  retryButton: {
    backgroundColor: '#2e7d32',
    paddingHorizontal: 10,
    paddingVertical: 6,
    borderRadius: 4,
  },
  retryText: {
    color: '#fff',
    fontSize: 12,
    fontWeight: '600',
  },
  actionButton: {
    backgroundColor: '#2e7d32',
    borderRadius: 8,
    paddingVertical: 12,
    paddingHorizontal: 15,
    marginBottom: 10,
  },
  actionButtonText: {
    color: '#fff',
    fontSize: 15,
    fontWeight: '600',
    textAlign: 'center',
  },
  infoSection: {
    padding: 15,
    backgroundColor: '#fff',
    marginVertical: 8,
    marginBottom: 20,
  },
  infoTitle: {
    fontSize: 16,
    fontWeight: '600',
    marginBottom: 12,
    color: '#333',
  },
  tipCard: {
    backgroundColor: '#f0f7f0',
    borderRadius: 8,
    padding: 12,
    marginBottom: 10,
    borderLeftWidth: 3,
    borderLeftColor: '#2e7d32',
  },
  tipTitle: {
    fontSize: 13,
    fontWeight: '600',
    color: '#2e7d32',
    marginBottom: 5,
  },
  tipText: {
    fontSize: 12,
    color: '#666',
    lineHeight: 18,
  },
});

export default HomeScreen;
