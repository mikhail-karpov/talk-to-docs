export interface User {
  id: string
  email: string
  firstName: string
  lastName: string
}

export interface SignInRequest {
  email: string
  password: string
}
