import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import AuthProvider from './auth/AuthProvider';
import ProtectedRoute from './auth/ProtectedRoute';
import RoleGuard from './auth/RoleGuard';

// Auth Pages
import LoginPage from './pages/auth/LoginPage';
import BuyerSignupPage from './pages/auth/BuyerSignupPage';
import SellerSignupPage from './pages/auth/SellerSignupPage';

// Buyer Pages
import BuyerDashboardPage from './pages/buyer/BuyerDashboardPage';

// Seller Pages
import SellerDashboardPage from './pages/seller/SellerDashboardPage';

// Public Pages
import ProductListingPage from './pages/public/ProductListingPage';

// Placeholder Pages
const AdminDashboardPage = () => <div className="p-10">Admin Dashboard (Coming Soon)</div>;
const HomePage = () => <div className="p-10">Home Page</div>;
const PublicLayout = ({ children }) => <>{children}</>;
const UnauthorizedPage = () => <div className="p-10 text-red-500">403 - Unauthorized Access</div>;

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          {/* Public Routes */}
          <Route path="/" element={<PublicLayout><HomePage /></PublicLayout>} />
          <Route path="/products" element={<PublicLayout><ProductListingPage /></PublicLayout>} />
          <Route path="/products/:id" element={<PublicLayout><div className="p-10">Product Details (Coming Soon)</div></PublicLayout>} />

          {/* Auth Routes */}
          <Route path="/auth/login" element={<LoginPage />} />
          <Route path="/auth/signup/buyer" element={<BuyerSignupPage />} />
          <Route path="/auth/signup/seller" element={<SellerSignupPage />} />
          <Route path="/unauthorized" element={<UnauthorizedPage />} />

          {/* Protected Routes */}

          {/* Buyer Routes */}
          <Route
            path="/buyer/dashboard"
            element={
              <ProtectedRoute>
                <RoleGuard allowedRoles={['BUYER']}>
                  <BuyerDashboardPage />
                </RoleGuard>
              </ProtectedRoute>
            }
          />

          {/* Seller Routes */}
          <Route
            path="/seller/dashboard"
            element={
              <ProtectedRoute>
                <RoleGuard allowedRoles={['SELLER']}>
                  <SellerDashboardPage />
                </RoleGuard>
              </ProtectedRoute>
            }
          />

          {/* Admin Routes */}
          <Route
            path="/admin/dashboard"
            element={
              <ProtectedRoute>
                <RoleGuard allowedRoles={['ADMIN']}>
                  <AdminDashboardPage />
                </RoleGuard>
              </ProtectedRoute>
            }
          />

          {/* Catch-all */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
