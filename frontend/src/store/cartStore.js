import { create } from 'zustand';
import cartApi from '../api/cartApi';
import { toast } from 'react-hot-toast';

const useCartStore = create((set, get) => ({
    cart: null,
    loading: false,
    error: null,
    itemLoading: {}, // Track loading state per item for granular UI feedback

    fetchCart: async () => {
        set({ loading: true, error: null });
        try {
            const response = await cartApi.getCart();
            set({ cart: response.data });
        } catch (error) {
            console.error('Failed to fetch cart:', error);
            set({ error: 'Failed to load cart' });
        } finally {
            set({ loading: false });
        }
    },

    addItem: async (productId, quantity = 1, shippingMode = 'AIR') => {
        set({ loading: true });
        try {
            await cartApi.addToCart({ productId, quantity, shippingMode });
            await get().fetchCart(); // Refresh cart to get updated totals/structure
            toast.success('Added to cart');
        } catch (error) {
            console.error('Failed to add item:', error);
            const msg = error.response?.data?.message || 'Failed to add item';
            toast.error(msg);
            throw error;
        } finally {
            set({ loading: false });
        }
    },

    updateItem: async (itemId, payload) => {
        // Optimistic update could go here, but for now we'll stick to safe backend-first
        set((state) => ({
            itemLoading: { ...state.itemLoading, [itemId]: true }
        }));

        try {
            await cartApi.updateCartItem(itemId, payload);
            await get().fetchCart();
        } catch (error) {
            console.error('Failed to update item:', error);
            toast.error('Failed to update item');
        } finally {
            set((state) => {
                const newItemLoading = { ...state.itemLoading };
                delete newItemLoading[itemId];
                return { itemLoading: newItemLoading };
            });
        }
    },

    removeItem: async (itemId) => {
        set((state) => ({
            itemLoading: { ...state.itemLoading, [itemId]: true }
        }));

        try {
            await cartApi.removeFromCart(itemId);
            await get().fetchCart();
            toast.success('Item removed');
        } catch (error) {
            console.error('Failed to remove item:', error);
            toast.error('Failed to remove item');
        } finally {
            set((state) => {
                const newItemLoading = { ...state.itemLoading };
                delete newItemLoading[itemId];
                return { itemLoading: newItemLoading };
            });
        }
    },

    clearCartState: () => set({ cart: null, error: null, loading: false })
}));

export default useCartStore;
