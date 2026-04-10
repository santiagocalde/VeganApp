import React from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Alert,
} from 'react-native';
import { useAuthStore } from '../../core/store/authStore';

const ProfileScreen = () => {
  const user = useAuthStore((state) => state.user);
  const logout = useAuthStore((state) => state.logout);

  const handleLogout = () => {
    Alert.alert(
      'Cerrar Sesión',
      '¿Estás seguro de que deseas cerrar sesión?',
      [
        { text: 'Cancelar', onPress: () => {}, style: 'cancel' },
        {
          text: 'Cerrar Sesión',
          onPress: () => logout(),
          style: 'destructive',
        },
      ]
    );
  };

  return (
    <ScrollView style={styles.container}>
      {/* Profile Header */}
      <View style={styles.header}>
        <View style={styles.avatar}>
          <Text style={styles.avatarText}>👤</Text>
        </View>
        <View style={styles.userInfo}>
          <Text style={styles.userName}>{user?.name || 'Usuario'}</Text>
          <Text style={styles.userEmail}>{user?.email || 'email@example.com'}</Text>
        </View>
      </View>

      {/* Profile Options */}
      <View style={styles.section}>
        <TouchableOpacity style={styles.optionButton}>
          <Text style={styles.optionIcon}>✏️</Text>
          <View style={styles.optionContent}>
            <Text style={styles.optionTitle}>Editar Perfil</Text>
            <Text style={styles.optionSubtitle}>Actualiza tu información</Text>
          </View>
        </TouchableOpacity>

        <TouchableOpacity style={styles.optionButton}>
          <Text style={styles.optionIcon}>🔐</Text>
          <View style={styles.optionContent}>
            <Text style={styles.optionTitle}>Cambiar Contraseña</Text>
            <Text style={styles.optionSubtitle}>Actualiza tu seguridad</Text>
          </View>
        </TouchableOpacity>

        <TouchableOpacity style={styles.optionButton}>
          <Text style={styles.optionIcon}>🔔</Text>
          <View style={styles.optionContent}>
            <Text style={styles.optionTitle}>Notificaciones</Text>
            <Text style={styles.optionSubtitle}>Configura preferencias</Text>
          </View>
        </TouchableOpacity>

        <TouchableOpacity style={styles.optionButton}>
          <Text style={styles.optionIcon}>📊</Text>
          <View style={styles.optionContent}>
            <Text style={styles.optionTitle}>Estadísticas</Text>
            <Text style={styles.optionSubtitle}>Ver tu progreso</Text>
          </View>
        </TouchableOpacity>
      </View>

      {/* Account Section */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Cuenta</Text>
        <View style={styles.accountInfo}>
          <View style={styles.infoRow}>
            <Text style={styles.infoLabel}>ID de Usuario</Text>
            <Text style={styles.infoValue}>{user?.id || 'N/A'}</Text>
          </View>
          <View style={styles.divider} />
          <View style={styles.infoRow}>
            <Text style={styles.infoLabel}>Estado</Text>
            <Text style={styles.infoValue}>Activo ✅</Text>
          </View>
        </View>
      </View>

      {/* Preferences Section */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Preferencias</Text>
        <TouchableOpacity style={styles.optionButton}>
          <Text style={styles.optionIcon}>🌍</Text>
          <View style={styles.optionContent}>
            <Text style={styles.optionTitle}>Idioma</Text>
            <Text style={styles.optionSubtitle}>Español</Text>
          </View>
        </TouchableOpacity>

        <TouchableOpacity style={styles.optionButton}>
          <Text style={styles.optionIcon}>🎨</Text>
          <View style={styles.optionContent}>
            <Text style={styles.optionTitle}>Tema</Text>
            <Text style={styles.optionSubtitle}>Modo claro</Text>
          </View>
        </TouchableOpacity>
      </View>

      {/* Logout Button */}
      <TouchableOpacity
        style={styles.logoutButton}
        onPress={handleLogout}
      >
        <Text style={styles.logoutText}>Cerrar Sesión</Text>
      </TouchableOpacity>

      {/* Footer */}
      <View style={styles.footer}>
        <Text style={styles.footerText}>VeganApp v1.0</Text>
        <Text style={styles.footerLink}>Términos y Condiciones</Text>
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
    alignItems: 'center',
    paddingBottom: 30,
  },
  avatar: {
    width: 80,
    height: 80,
    borderRadius: 40,
    backgroundColor: '#fff',
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 12,
  },
  avatarText: {
    fontSize: 40,
  },
  userInfo: {
    alignItems: 'center',
  },
  userName: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#fff',
  },
  userEmail: {
    fontSize: 13,
    color: '#e8f5e9',
    marginTop: 4,
  },
  section: {
    backgroundColor: '#fff',
    marginVertical: 8,
    paddingVertical: 8,
  },
  sectionTitle: {
    fontSize: 14,
    fontWeight: '600',
    color: '#666',
    paddingHorizontal: 15,
    paddingVertical: 10,
    textTransform: 'uppercase',
    letterSpacing: 0.5,
  },
  optionButton: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 15,
    paddingVertical: 12,
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
  },
  optionIcon: {
    fontSize: 24,
    marginRight: 12,
  },
  optionContent: {
    flex: 1,
  },
  optionTitle: {
    fontSize: 14,
    fontWeight: '600',
    color: '#333',
  },
  optionSubtitle: {
    fontSize: 12,
    color: '#999',
    marginTop: 3,
  },
  accountInfo: {
    paddingHorizontal: 15,
    paddingVertical: 12,
  },
  infoRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingVertical: 8,
  },
  infoLabel: {
    fontSize: 13,
    color: '#666',
    fontWeight: '500',
  },
  infoValue: {
    fontSize: 13,
    color: '#333',
    fontWeight: '600',
  },
  divider: {
    height: 1,
    backgroundColor: '#f0f0f0',
    marginVertical: 8,
  },
  logoutButton: {
    backgroundColor: '#c62828',
    marginHorizontal: 15,
    marginVertical: 20,
    paddingVertical: 12,
    borderRadius: 8,
    alignItems: 'center',
  },
  logoutText: {
    color: '#fff',
    fontSize: 15,
    fontWeight: '600',
  },
  footer: {
    alignItems: 'center',
    paddingVertical: 20,
  },
  footerText: {
    fontSize: 12,
    color: '#999',
    marginBottom: 5,
  },
  footerLink: {
    fontSize: 11,
    color: '#2e7d32',
    textDecorationLine: 'underline',
  },
});

export default ProfileScreen;
