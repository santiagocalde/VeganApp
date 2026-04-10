import apiClient from './client';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  user: {
    id: string;
    email: string;
    name: string;
  };
}

export interface HealthResponse {
  status: string;
  timestamp: string;
}

const authService = {
  // Test connection to backend
  async health(): Promise<HealthResponse> {
    const { data } = await apiClient.get('/health');
    return data;
  },

  // Login user
  async login(credentials: LoginRequest): Promise<LoginResponse> {
    const { data } = await apiClient.post('/auth/login', credentials);
    return data;
  },

  // Register new user
  async register(email: string, password: string, name: string) {
    const { data } = await apiClient.post('/auth/register', {
      email,
      password,
      name,
    });
    return data;
  },

  // Refresh token
  async refreshToken(token: string) {
    const { data } = await apiClient.post('/auth/refresh', { token });
    return data;
  },
};

export default authService;
