package com.ubt.alpha1e_edu.business.thrid_party;

import twitter4j.auth.AccessToken;

public interface ITwitterLoginListener {
	public void OnLoginComplete(AccessToken accessToken);

}
