import api from './api';
import { API_BASE_URL } from '../config/constants';

const API_URL = `${API_BASE_URL}/payments`;

// Get all payments with optional filters
export const getAllPayments = async (filters = {}) => {
  try {
    const response = await api.get('/payments', { params: filters });
    console.log('Payment data from server:', JSON.stringify(response.data));
    return response.data;
  } catch (error) {
    console.error('Error fetching payments:', error);
    throw error;
  }
};

// Get a payment by ID
export const getPaymentById = async (id) => {
  try {
    const response = await api.get(`/payments/${id}`);
    console.log(`Payment ${id} details:`, JSON.stringify(response.data));
    return response.data;
  } catch (error) {
    console.error(`Error fetching payment with ID ${id}:`, error);
    throw error;
  }
};

// Create a new payment
export const createPayment = async (paymentData) => {
  try {
    // Format data to match backend expectations
    // The backend expects a Payment object with household.id and fee.id
    const payload = {
      household: { id: paymentData.householdId },
      fee: { id: paymentData.feeId },
      amount: paymentData.amount,
      amountPaid: paymentData.amountPaid || paymentData.amount, // Default to amount if not specified
      paymentDate: paymentData.paymentDate,
      verified: paymentData.verified || false,
      notes: paymentData.notes || ''
    };
    
    console.log('Creating payment with payload:', JSON.stringify(payload));
    const response = await api.post('/payments', payload);
    console.log('Payment created successfully:', response.data);
    // Log the structure of the response data
    console.log('Response data structure (DTO):', {
      id: response.data.id,
      householdId: response.data.householdId,
      householdOwnerName: response.data.householdOwnerName,
      householdAddress: response.data.householdAddress,
      feeId: response.data.feeId,
      feeName: response.data.feeName,
      feeAmount: response.data.feeAmount
    });
    return response.data;
  } catch (error) {
    console.error('Error creating payment:', error);
    console.error('Payment payload that caused error:', JSON.stringify(paymentData));
    if (error.response) {
      console.error('Response error:', error.response.status, error.response.data);
    }
    throw error;
  }
};

// Update an existing payment
export const updatePayment = async (id, paymentData) => {
  try {
    // Format data to match backend expectations
    const payload = {
      household: { id: paymentData.householdId },
      fee: { id: paymentData.feeId },
      amount: paymentData.amount,
      amountPaid: paymentData.amountPaid || paymentData.amount, // Default to amount if not specified
      paymentDate: paymentData.paymentDate,
      verified: paymentData.verified || false,
      notes: paymentData.notes || ''
    };
    
    console.log(`Updating payment ${id} with payload:`, JSON.stringify(payload));
    const response = await api.put(`/payments/${id}`, payload);
    console.log('Payment updated successfully:', response.data);
    return response.data;
  } catch (error) {
    console.error(`Error updating payment with ID ${id}:`, error);
    console.error('Update payload that caused error:', JSON.stringify(paymentData));
    if (error.response) {
      console.error('Response error:', error.response.status, error.response.data);
    }
    throw error;
  }
};

// Toggle payment verification
export const togglePaymentVerification = async (id, verified) => {
  try {
    if (verified) {
      await api.put(`/payments/${id}/verify`);
    } else {
      await api.put(`/payments/${id}/unverify`);
    }
    return { success: true };
  } catch (error) {
    console.error(`Error toggling verification for payment with ID ${id}:`, error);
    throw error;
  }
};

// Delete a payment
export const deletePayment = async (id) => {
  try {
    await api.delete(`/payments/${id}`);
    return { success: true };
  } catch (error) {
    console.error(`Error deleting payment with ID ${id}:`, error);
    throw error;
  }
};

export const getPaymentsByHousehold = async (householdId) => {
  try {
    const response = await api.get(`/payments/household/${householdId}`);
    return response.data;
  } catch (error) {
    console.error(`Get payments by household ${householdId} error:`, error);
    throw error;
  }
};

export const getPaymentsByFee = async (feeId) => {
  try {
    const response = await api.get(`/payments/fee/${feeId}`);
    return response.data;
  } catch (error) {
    console.error(`Get payments by fee ${feeId} error:`, error);
    throw error;
  }
};

export const getPaymentsByDateRange = async (startDate, endDate) => {
  try {
    const response = await api.get(`/payments/date-range`, {
      params: { startDate, endDate }
    });
    return response.data;
  } catch (error) {
    console.error('Get payments by date range error:', error);
    throw error;
  }
};

export const getUnverifiedPayments = async () => {
  try {
    const response = await api.get(`/payments/unverified`);
    return response.data;
  } catch (error) {
    console.error('Get unverified payments error:', error);
    throw error;
  }
};

export const getPaymentByHouseholdAndFee = async (householdId, feeId) => {
  try {
    const response = await api.get(`/payments/household/${householdId}/fee/${feeId}`);
    return response.data;
  } catch (error) {
    console.error(`Get payment by household ${householdId} and fee ${feeId} error:`, error);
    throw error;
  }
};

export const verifyPayment = async (id) => {
  try {
    await api.put(`/payments/${id}/verify`);
    return true;
  } catch (error) {
    console.error(`Verify payment ${id} error:`, error);
    throw error;
  }
};

export const unverifyPayment = async (id) => {
  try {
    await api.put(`/payments/${id}/unverify`);
    return true;
  } catch (error) {
    console.error(`Unverify payment ${id} error:`, error);
    throw error;
  }
};

// Statistics 
export const getTotalPaymentsByHousehold = async (householdId) => {
  try {
    const response = await api.get(`/payments/statistics/household/${householdId}/total`);
    return response.data;
  } catch (error) {
    console.error(`Get total payments by household ${householdId} error:`, error);
    throw error;
  }
};

export const getTotalPaymentsByFee = async (feeId) => {
  try {
    const response = await api.get(`/payments/statistics/fee/${feeId}/total`);
    return response.data;
  } catch (error) {
    console.error(`Get total payments by fee ${feeId} error:`, error);
    throw error;
  }
};

export const getTotalPaymentsByDateRange = async (startDate, endDate) => {
  try {
    const response = await api.get(`/payments/statistics/date-range/total`, {
      params: { startDate, endDate }
    });
    return response.data;
  } catch (error) {
    console.error('Get total payments by date range error:', error);
    throw error;
  }
}; 