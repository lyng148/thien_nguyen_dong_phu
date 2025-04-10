import api from './api';

// Get all fees with optional filters
export const getAllFees = async (filters = {}) => {
  try {
    // Always include showAll=true to get all fees regardless of active status
    const params = { ...filters, showAll: true };
    const response = await api.get('/fees', { params });
    return response.data;
  } catch (error) {
    console.error('Error fetching fees:', error);
    throw error;
  }
};

// Get a fee by ID
export const getFeeById = async (id) => {
  try {
    const response = await api.get(`/fees/${id}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching fee with ID ${id}:`, error);
    throw error;
  }
};

// Create a new fee
export const createFee = async (feeData) => {
  try {
    const response = await api.post('/fees', feeData);
    return response.data;
  } catch (error) {
    console.error('Error creating fee:', error);
    throw error;
  }
};

// Update a fee
export const updateFee = async (id, feeData) => {
  try {
    const response = await api.put(`/fees/${id}`, feeData);
    return response.data;
  } catch (error) {
    console.error(`Error updating fee with ID ${id}:`, error);
    throw error;
  }
};

// Toggle fee active status
export const toggleFeeStatus = async (id, active) => {
  try {
    console.log(`Making API call to toggle fee ${id} to ${active ? 'active' : 'inactive'}`);
    const response = await api.patch(`/fees/${id}/status`, { active });
    console.log(`API response for fee toggle:`, response);
    return response.data;
  } catch (error) {
    console.error(`Error toggling status for fee with ID ${id}:`, error);
    if (error.response) {
      console.error('Response status:', error.response.status);
      console.error('Response data:', error.response.data);
    }
    throw error;
  }
};

// Delete a fee (deactivate)
export const deleteFee = async (id) => {
  try {
    await api.delete(`/fees/${id}`);
    return { success: true };
  } catch (error) {
    console.error(`Error deleting fee with ID ${id}:`, error);
    throw error;
  }
};

// Get payments for a fee
export const getFeePayments = async (id) => {
  try {
    const response = await api.get(`/payments/fee/${id}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching payments for fee with ID ${id}:`, error);
    throw error;
  }
};

// Get statistics for a fee
export const getFeeStatistics = async (id) => {
  try {
    const response = await api.get(`/fees/${id}/statistics`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching statistics for fee with ID ${id}:`, error);
    throw error;
  }
};

// Get fees by type
export const getFeesByType = async (type) => {
  try {
    const response = await api.get(`/fees/type/${type}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching fees of type ${type}:`, error);
    throw error;
  }
}; 