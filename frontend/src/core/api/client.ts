import axios from 'axios';

// API configuration
const API_BASE_URL = __DEV__
  ? 'http://10.0.2.2:8080/api' // Android emulator: 10.0.2.2 routes to host machine
  : 'https://api.veganapp.com/api'; // Production URL

// Create Axios instance with default config
export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor for adding auth token
apiClient.interceptors.request.use(
  (config) => {
    // Token will be added here from auth store
    // const token = authStore.getToken();
    // if (token) {
    //   config.headers.Authorization = `Bearer ${token}`;
    // }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor for error handling
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Handle unauthorized - clear auth state
      // authStore.clearAuth();
    }
    return Promise.reject(error);
  }
);

export default apiClient;
