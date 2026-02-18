import React from 'react';
import { useRoutes, BrowserRouter } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import AuthProvider from './auth/AuthProvider';
import { appRoutes } from './routes';

const AppContent = () => {
  const content = useRoutes(appRoutes);
  return content;
};

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Toaster
          position="top-center"
          toastOptions={{
            duration: 4000,
            style: {
              background: '#334155',
              color: '#fff',
              borderRadius: '12px',
              fontWeight: 'bold',
            },
          }}
        />
        <AppContent />
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
