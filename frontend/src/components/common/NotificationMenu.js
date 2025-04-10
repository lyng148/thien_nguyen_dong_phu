import React, { useState, useEffect } from 'react';
import {
  IconButton,
  Badge,
  Menu,
  MenuItem,
  ListItemText,
  Typography,
  Box,
  Divider,
  CircularProgress,
  Paper
} from '@mui/material';
import {
  Notifications as NotificationsIcon,
  Circle as UnreadIcon
} from '@mui/icons-material';
import { format } from 'date-fns';
import notificationService from '../../services/notificationService';
import { useNavigate } from 'react-router-dom';

const NotificationMenu = () => {
  console.log('NotificationMenu rendered'); // Debug log
  
  const navigate = useNavigate();
  const [anchorEl, setAnchorEl] = useState(null);
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const open = Boolean(anchorEl);

  useEffect(() => {
    console.log('NotificationMenu useEffect running'); // Debug log
    fetchNotifications();
    const interval = setInterval(fetchNotifications, 30000); // Refresh every 30 seconds
    return () => clearInterval(interval);
  }, []);

  const fetchNotifications = async () => {
    console.log('Starting to fetch notifications');
    setLoading(true);
    setError(null);
    try {
      // Get unread notifications
      console.log('Fetching unread notifications');
      const notifs = await notificationService.getUnreadNotifications();
      console.log('Notifications received:', notifs);
      setNotifications(notifs);
      
      // Get unread count
      console.log('Fetching unread count');
      const count = await notificationService.getUnreadCount();
      console.log('Unread count received:', count);
      setUnreadCount(count);
    } catch (error) {
      console.error('Error fetching notifications:', error);
      setError('Failed to load notifications');
    } finally {
      setLoading(false);
      console.log('Finished fetching notifications');
    }
  };

  const handleClick = (event) => {
    console.log('Notification icon clicked', event.currentTarget);
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    console.log('Closing notification menu');
    setAnchorEl(null);
  };

  const handleNotificationClick = async (notification) => {
    try {
      await notificationService.markAsRead(notification.id);
      handleClose();
      
      // Navigate based on entity type
      if (notification.entityId) {
        switch (notification.entityType) {
          case 'FEE':
            navigate(`/fees/${notification.entityId}`);
            break;
          case 'HOUSEHOLD':
            navigate(`/households/${notification.entityId}`);
            break;
          case 'PAYMENT':
            navigate(`/payments/${notification.entityId}`);
            break;
          default:
            // Nếu không có entityType phù hợp, chỉ chuyển hướng đến trang danh sách tương ứng
            navigateToListPage(notification.entityType);
            break;
        }
      } else {
        // Nếu không có entityId, chuyển hướng đến trang danh sách tương ứng
        navigateToListPage(notification.entityType);
      }
      
      fetchNotifications();
    } catch (error) {
      console.error('Error handling notification:', error);
      setError('Failed to mark notification as read');
      // Vẫn chuyển hướng đến trang danh sách nếu có lỗi
      navigateToListPage(notification.entityType);
    }
  };

  const navigateToListPage = (entityType) => {
    switch (entityType) {
      case 'FEE':
        navigate('/fees');
        break;
      case 'HOUSEHOLD':
        navigate('/households');
        break;
      case 'PAYMENT':
        navigate('/payments');
        break;
      default:
        navigate('/dashboard');
        break;
    }
  };

  console.log('Rendering NotificationMenu, open:', open, 'anchorEl:', anchorEl); // Debug log

  return (
    <>
      <IconButton
        color="inherit"
        onClick={handleClick}
        size="large"
      >
        <Badge badgeContent={unreadCount} color="error">
          <NotificationsIcon />
        </Badge>
      </IconButton>
      
      <Menu
        id="notification-menu"
        anchorEl={anchorEl}
        open={open}
        onClose={handleClose}
        MenuListProps={{
          'aria-labelledby': 'notification-button',
        }}
        PaperProps={{
          elevation: 3,
          sx: {
            width: 360,
            maxHeight: 400,
            overflow: 'auto',
          },
        }}
        transformOrigin={{ horizontal: 'right', vertical: 'top' }}
        anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
      >
        <Box sx={{ p: 2 }}>
          <Typography variant="h6">Notifications</Typography>
        </Box>
        <Divider />
        
        {loading && (
          <Box sx={{ display: 'flex', justifyContent: 'center', p: 2 }}>
            <CircularProgress size={24} />
          </Box>
        )}
        
        {error && (
          <MenuItem disabled>
            <ListItemText primary={error} sx={{ color: 'error.main' }} />
          </MenuItem>
        )}
        
        {!loading && !error && notifications.length === 0 && (
          <MenuItem disabled>
            <ListItemText primary="No new notifications" />
          </MenuItem>
        )}
        
        {!loading && !error && notifications.length > 0 && notifications.map((notification) => (
          <MenuItem
            key={notification.id}
            onClick={() => handleNotificationClick(notification)}
            sx={{
              display: 'block',
              borderBottom: '1px solid rgba(0, 0, 0, 0.12)',
              py: 1,
            }}
          >
            <Box display="flex" alignItems="center" width="100%">
              {!notification.read && (
                <UnreadIcon
                  color="primary"
                  sx={{ fontSize: 12, mr: 1 }}
                />
              )}
              <Typography variant="subtitle2" sx={{ flex: 1, fontWeight: 600 }}>
                {notification.title}
              </Typography>
              <Typography variant="caption" color="text.secondary">
                {format(new Date(notification.createdAt), 'MMM d, HH:mm')}
              </Typography>
            </Box>
            <Typography
              variant="body2"
              color="text.secondary"
              sx={{ mt: 0.5 }}
            >
              {notification.message}
            </Typography>
          </MenuItem>
        ))}
      </Menu>
    </>
  );
};

export default NotificationMenu; 