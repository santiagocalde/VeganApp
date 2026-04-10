import apiClient from './client';

export interface MealPlan {
  id: string;
  date: string;
  recipeId: string;
  servings: number;
  meals: 'breakfast' | 'lunch' | 'dinner' | 'snack';
}

const plannerService = {
  async getMealPlan(startDate: string, endDate: string): Promise<MealPlan[]> {
    const { data } = await apiClient.get('/planner', {
      params: { startDate, endDate },
    });
    return data;
  },

  async addMeal(meal: Omit<MealPlan, 'id'>): Promise<MealPlan> {
    const { data } = await apiClient.post('/planner', meal);
    return data;
  },

  async removeMeal(id: string): Promise<void> {
    await apiClient.delete(`/planner/${id}`);
  },

  async generateShoppingList(startDate: string, endDate: string): Promise<void> {
    await apiClient.post('/planner/shopping-list', { startDate, endDate });
  },
};

export default plannerService;
