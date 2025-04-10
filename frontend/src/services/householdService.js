import api from './api';

// Get all households with optional filters
export const getAllHouseholds = async (filters = {}) => {
  try {
    console.log('Fetching households with filters:', filters);
    const response = await api.get('/households', { params: filters });
    console.log('API response for households:', response);
    
    if (!response.data) {
      console.error('API response has no data property:', response);
      return [];
    }
    
    // Handle case where the data might be a string (JSON parse error from backend)
    let households = response.data;
    
    // If the response is a string, try to parse it as JSON
    if (typeof response.data === 'string') {
      try {
        console.log('Response data is a string, attempting to parse');
        // If the backend returns a string due to serialization error, try to extract just the households array
        // Looking for something that resembles JSON array structure
        const match = response.data.match(/\[\s*\{\s*"id".*?\}\s*\]/s);
        if (match) {
          const jsonStr = match[0];
          households = JSON.parse(jsonStr);
          console.log('Successfully parsed households from string');
        } else {
          console.error('Could not find households array in response string');
          return [];
        }
      } catch (parseError) {
        console.error('Error parsing response data as JSON:', parseError);
        return [];
      }
    }
    
    if (!Array.isArray(households)) {
      console.error('API response data is not an array:', households);
      
      // If it's an object with a property that could be the households array, try to use that
      if (households && typeof households === 'object') {
        // Look for any property that is an array
        const arrayProps = Object.keys(households).filter(key => 
          Array.isArray(households[key])
        );
        
        if (arrayProps.length > 0) {
          console.log('Found array property:', arrayProps[0]);
          households = households[arrayProps[0]];
        } else {
          // If it's just a single household object, wrap it in an array
          if (households.id) {
            console.log('Converting single household to array');
            households = [households];
          } else {
            return [];
          }
        }
      } else {
        return [];
      }
    }
    
    if (households.length === 0) {
      console.log('API returned empty households array');
    } else {
      console.log('Successfully fetched', households.length, 'households');
    }
    
    return households;
  } catch (error) {
    console.error('Error fetching households:', error);
    console.error('Error details:', {
      message: error.message,
      response: error.response?.data,
      status: error.response?.status
    });
    return []; // Return empty array instead of throwing to avoid UI errors
  }
};

// Get a household by ID
export const getHouseholdById = async (id) => {
  try {
    const response = await api.get(`/households/${id}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching household with ID ${id}:`, error);
    throw error;
  }
};

// Create a new household
export const createHousehold = async (householdData) => {
  try {
    console.log('Creating household with data:', householdData);
    const response = await api.post('/households', householdData);
    console.log('Household created response:', response);
    return response.data;
  } catch (error) {
    console.error('Error creating household:', error);
    throw error;
  }
};

// Update a household
export const updateHousehold = async (id, householdData) => {
  try {
    const response = await api.put(`/households/${id}`, householdData);
    return response.data;
  } catch (error) {
    console.error(`Error updating household with ID ${id}:`, error);
    throw error;
  }
};

// Delete a household (deactivate)
export const deleteHousehold = async (id) => {
  try {
    await api.delete(`/households/${id}`);
    return true;
  } catch (error) {
    console.error(`Error deleting household with ID ${id}:`, error);
    throw error;
  }
};

// Activate a household
export const activateHousehold = async (id) => {
  try {
    await api.put(`/households/${id}/activate`);
    return true;
  } catch (error) {
    console.error(`Error activating household with ID ${id}:`, error);
    throw error;
  }
};

// Search households by owner name or address
export const searchHouseholds = async (searchParams) => {
  try {
    const response = await api.get('/households/search', { params: searchParams });
    return response.data;
  } catch (error) {
    console.error('Error searching households:', error);
    throw error;
  }
};

// Get household payment history
export const getHouseholdPaymentHistory = async (id) => {
  try {
    const response = await api.get(`/households/${id}/payments`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching payment history for household with ID ${id}:`, error);
    throw error;
  }
};

// Get household statistics
export const getHouseholdStatistics = async (id) => {
  try {
    const response = await api.get(`/households/${id}/statistics`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching statistics for household with ID ${id}:`, error);
    throw error;
  }
}; 