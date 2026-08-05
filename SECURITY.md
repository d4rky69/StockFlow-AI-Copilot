If a secret (API key, token, or credential) is accidentally committed to this
repository, revoke it immediately and rotate to a new credential.

Recommended steps:

- Revoke the compromised key in Google Cloud Console: go to APIs & Services ->
  Credentials, find the key and delete or restrict it.
- Create a new key with the minimum necessary permissions.
- Replace usage with an environment variable or a secret manager (see .env.exa
mple).
- Check the repository for other leaked secrets and force-push a cleaned histo
ry if needed.
- Update any deployments/CI that used the old secret to the new one.

If you want help rotating the key or scanning the repo, ask and I can assist.
