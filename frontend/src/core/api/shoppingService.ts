import apiClient from './client';

export interface ShoppingItem {
  id: string;
  name: string;
  quantity: number;
  unit: string;
  category: string;
  isChecked: boolean;
}

const shoppingListService = {
  async getItems(): Promise<ShoppingItem[]> {
    const { data } = await apiClient.get('/shopping-list');
    return data;
  },

  async addItem(item: Omit<ShoppingItem, 'id'>): Promise<ShoppingItem> {
    const { data } = await apiClient.post('/shopping-list', item);
    return data;
  },

  async updateItem(id: string, item: Partial<ShoppingItem>): Promise<ShoppingItem> {
    const { data } = await apiClient.put(`/shopping-list/${id}`, item);
    return data;
  },

  async deleteItem(id: string): Promise<void> {
    await apiClient.delete(`/shopping-list/${id}`);
  },

  async clearChecked(): Promise<void> {
    await apiClient.delete('/shopping-list/checked');
  },
};

export default shoppingListService;
