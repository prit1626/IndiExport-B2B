import React from 'react';
import { Navigate } from 'react-router-dom';

// Layouts
import PublicLayout from './layouts/PublicLayout';
import BuyerLayout from './layouts/BuyerLayout';
import SellerLayout from './layouts/SellerLayout';
import AdminLayout from './layouts/AdminLayout';

// Auth Pages
import LoginPage from './pages/auth/LoginPage';
import BuyerSignupPage from './pages/auth/BuyerSignupPage';
import SellerSignupPage from './pages/auth/SellerSignupPage';

// Public Pages
import ProductListingPage from './pages/public/ProductListingPage';
import ProductDetailsPage from './pages/public/ProductDetailsPage';
import PublicSellerProfilePage from './pages/public/PublicSellerProfilePage';
import TermsPage from './pages/public/TermsPage';

// Buyer Pages
import BuyerDashboardPage from './pages/buyer/BuyerDashboardPage';
import CartPage from './pages/buyer/CartPage';
import CheckoutPage from './pages/buyer/CheckoutPage';
import PaymentPage from './pages/buyer/PaymentPage';
import OrdersPage from './pages/buyer/OrdersPage';
import OrderDetailsPage from './pages/buyer/OrderDetailsPage';
import OrderTrackingPage from './pages/buyer/OrderTrackingPage';
import BuyerInquiriesPage from './pages/buyer/BuyerInquiriesPage';
import BuyerInquiryChatPage from './pages/buyer/BuyerInquiryChatPage';
import BuyerDisputesPage from './pages/buyer/BuyerDisputesPage';
import BuyerProfilePage from './pages/buyer/BuyerProfilePage';

// Seller Pages
import SellerDashboardPage from './pages/seller/SellerDashboardPage';
import SellerProductsPage from './pages/seller/SellerProductsPage';
import SellerProductCreatePage from './pages/seller/SellerProductCreatePage';
import SellerProductEditPage from './pages/seller/SellerProductEditPage';
import SellerOrdersPage from './pages/seller/SellerOrdersPage';
import SellerOrderDetailsPage from './pages/seller/SellerOrderDetailsPage';
import SellerInquiriesPage from './pages/seller/SellerInquiriesPage';
import SellerInquiryChatPage from './pages/seller/SellerInquiryChatPage';
import SellerDisputesPage from './pages/seller/SellerDisputesPage';
import SellerVerificationPage from './pages/seller/SellerVerificationPage';
import SellerDocumentsUploadPage from './pages/seller/SellerDocumentsUploadPage';
import SellerProfilePage from './pages/seller/SellerProfilePage';

// Admin Pages
import AdminDashboardPage from './pages/admin/AdminDashboardPage';
import AdminDisputesPage from './pages/admin/AdminDisputesPage';
import AdminSettingsPage from './pages/admin/AdminSettingsPage';
import AdminTermsEditorPage from './pages/admin/AdminTermsEditorPage';
import AdminSellerVerificationPage from './pages/admin/AdminSellerVerificationPage';
import AdminSellerDetailsPage from './pages/admin/AdminSellerDetailsPage';

// Common Pages
import NotFoundPage from './pages/common/NotFoundPage';
import AccessDeniedPage from './pages/common/AccessDeniedPage';

// Placeholder/Missing Pages (User mentioned these in architecture but they might not exist yet)
const SellerUpgradePage = () => <div className="p-8"><h1 className="text-2xl font-bold">Upgrade Your Plan</h1><p className="mt-4 text-slate-500">Coming soon.</p></div>;
const SellerBillingPage = () => <div className="p-8"><h1 className="text-2xl font-bold">Billing & Invoices</h1><p className="mt-4 text-slate-500">Coming soon.</p></div>;
const AdminUsersPage = () => <div className="p-8"><h1 className="text-2xl font-bold">Manage Users</h1><p className="mt-4 text-slate-500">Coming soon.</p></div>;
const AdminProductsPage = () => <div className="p-8"><h1 className="text-2xl font-bold">Products Audit</h1><p className="mt-4 text-slate-500">Coming soon.</p></div>;
const HomePage = () => <Navigate to="/products" replace />; // Default home to products for now

// Protected Route components
import ProtectedRoute from './auth/ProtectedRoute';
import RoleGuard from './auth/RoleGuard';

export const appRoutes = [
    {
        path: '/',
        element: <PublicLayout />,
        children: [
            { index: true, element: <HomePage /> },
            { path: 'products', element: <ProductListingPage /> },
            { path: 'products/:id', element: <ProductDetailsPage /> },
            { path: 'sellers/:id', element: <PublicSellerProfilePage /> },
            { path: 'terms', element: <TermsPage /> },
            { path: 'auth/login', element: <LoginPage /> },
            { path: 'auth/signup/buyer', element: <BuyerSignupPage /> },
            { path: 'auth/signup/seller', element: <SellerSignupPage /> },
        ]
    },
    {
        path: '/buyer',
        element: <ProtectedRoute><RoleGuard allowedRoles={['BUYER']}><BuyerLayout /></RoleGuard></ProtectedRoute>,
        children: [
            { path: 'dashboard', element: <BuyerDashboardPage /> },
            { path: 'cart', element: <CartPage /> },
            { path: 'checkout', element: <CheckoutPage /> },
            { path: 'orders', element: <OrdersPage /> },
            { path: 'orders/:id', element: <OrderDetailsPage /> },
            { path: 'orders/:id/pay', element: <PaymentPage /> },
            { path: 'orders/:id/tracking', element: <OrderTrackingPage /> },
            { path: 'inquiries', element: <BuyerInquiriesPage /> },
            { path: 'inquiries/:chatId', element: <BuyerInquiryChatPage /> },
            { path: 'disputes', element: <BuyerDisputesPage /> },
            { path: 'profile', element: <BuyerProfilePage /> },
        ]
    },
    {
        path: '/seller',
        element: <ProtectedRoute><RoleGuard allowedRoles={['SELLER']}><SellerLayout /></RoleGuard></ProtectedRoute>,
        children: [
            { path: 'dashboard', element: <SellerDashboardPage /> },
            { path: 'products', element: <SellerProductsPage /> },
            { path: 'products/new', element: <SellerProductCreatePage /> },
            { path: 'products/:id/edit', element: <SellerProductEditPage /> },
            { path: 'orders', element: <SellerOrdersPage /> },
            { path: 'orders/:id', element: <SellerOrderDetailsPage /> },
            { path: 'inquiries', element: <SellerInquiriesPage /> },
            { path: 'inquiries/:chatId', element: <SellerInquiryChatPage /> },
            { path: 'disputes', element: <SellerDisputesPage /> },
            { path: 'verification', element: <SellerVerificationPage /> },
            { path: 'verification/upload', element: <SellerDocumentsUploadPage /> },
            { path: 'upgrade', element: <SellerUpgradePage /> },
            { path: 'billing', element: <SellerBillingPage /> },
            { path: 'profile', element: <SellerProfilePage /> },
        ]
    },
    {
        path: '/admin',
        element: <ProtectedRoute><RoleGuard allowedRoles={['ADMIN']}><AdminLayout /></RoleGuard></ProtectedRoute>,
        children: [
            { path: 'dashboard', element: <AdminDashboardPage /> },
            { path: 'users', element: <AdminUsersPage /> },
            { path: 'sellers/verification', element: <AdminSellerVerificationPage /> },
            { path: 'sellers/:id', element: <AdminSellerDetailsPage /> },
            { path: 'products', element: <AdminProductsPage /> },
            { path: 'disputes', element: <AdminDisputesPage /> },
            { path: 'settings', element: <AdminSettingsPage /> },
            { path: 'terms', element: <AdminTermsEditorPage /> },
        ]
    },
    { path: '/403', element: <AccessDeniedPage /> },
    { path: '*', element: <NotFoundPage /> }
];
