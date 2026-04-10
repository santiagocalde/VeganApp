import apiClient from './client';

export interface Recipe {
  id: string;
  name: string;
  description: string;
  ingredients: string[];
  steps: string[];
  servings: number;
  prepTime: number;
  cookTime: number;
}

const recipeService = {
  async getAll(): Promise<Recipe[]> {
    const { data } = await apiClient.get('/recipes');
    return data;
  },

  async getById(id: string): Promise<Recipe> {
    const { data } = await apiClient.get(`/recipes/${id}`);
    return data;
  },

  async create(recipe: Omit<Recipe, 'id'>): Promise<Recipe> {
    const { data } = await apiClient.post('/recipes', recipe);
    return data;
  },

  async update(id: string, recipe: Partial<Recipe>): Promise<Recipe> {
    const { data } = await apiClient.put(`/recipes/${id}`, recipe);
    return data;
  },

  async delete(id: string): Promise<void> {
    await apiClient.delete(`/recipes/${id}`);
  },
};

export default recipeService;
