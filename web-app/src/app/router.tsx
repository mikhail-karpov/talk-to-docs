import { createBrowserRouter } from 'react-router'
import AppLayout from '@/components/layout/app-layout'
import { PrivateRoute } from './private-route'
import HomeRoute from './routes/home'
import SignInRoute from './routes/sign-in'

export function createRouter() {
  return createBrowserRouter([
    {
      element: <PrivateRoute />,
      children: [
        {
          element: <AppLayout />,
          children: [
            {
              path: '/',
              element: <HomeRoute />,
            },
          ],
        },
      ],
    },
    {
      path: '/sign-in',
      element: <SignInRoute />,
    },
  ])
}
