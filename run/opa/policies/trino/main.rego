package trino

default allow = true

groups = {
    "alice": ["admins", "analytics"],
    "bob": ["users"],
    "trino": ["system"]
}[input.user]
