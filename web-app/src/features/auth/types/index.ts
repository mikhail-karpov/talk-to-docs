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

export interface SignUpRequest {
  email: string
  password: string
  firstName: string
  lastName: string
}
